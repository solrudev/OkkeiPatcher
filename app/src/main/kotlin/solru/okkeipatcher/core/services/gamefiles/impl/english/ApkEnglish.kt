package solru.okkeipatcher.core.services.gamefiles.impl.english

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.model.Language
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.model.files.english.FileHashKey
import solru.okkeipatcher.core.model.files.english.FilesEnglish
import solru.okkeipatcher.core.services.gamefiles.impl.BaseApk
import solru.okkeipatcher.core.strategy.impl.english.FileVersionKey
import solru.okkeipatcher.core.strategy.impl.english.PatchFile
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.manifest.OkkeiManifest
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.io.services.StreamCopier
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.deleteTempZipFiles
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.isPackageInstalled
import solru.okkeipatcher.utils.use
import java.io.File
import javax.inject.Inject

class ApkEnglish @Inject constructor(
	private val files: FilesEnglish,
	private val httpDownloader: HttpDownloader,
	commonFiles: CommonFiles,
	streamCopier: StreamCopier,
	ioDispatcher: CoroutineDispatcher
) : BaseApk(commonFiles, streamCopier, ioDispatcher) {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, files.scripts.progress)

	override suspend fun patch(manifest: OkkeiManifest) {
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_apk))
		if (verifyBackupIntegrity() && commonFiles.signedApk.verify()) {
			installPatched()
			return
		}
		var extractedScriptsDirectory: File? = null
		try {
			if (!isPackageInstalled(PACKAGE_NAME)) {
				throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
			}
			copyOriginalApkTo(commonFiles.tempApk)
			downloadScripts(manifest)
			mutableStatus.emit(LocalizedString.resource(R.string.status_extracting_scripts))
			extractedScriptsDirectory = extractScripts()
			mutableStatus.emit(LocalizedString.resource(R.string.status_replacing_scripts))
			val apkZip = ZipFile(commonFiles.tempApk.fullPath).apply { isRunInThread = true }
			apkZip.use {
				replaceScripts(it, extractedScriptsDirectory)
				removeSignature(it)
			}
			sign()
			installPatched()
		} finally {
			deleteTempZipFiles(OkkeiStorage.external)
			extractedScriptsDirectory?.let { if (it.exists()) it.deleteRecursively() }
			commonFiles.tempApk.delete()
		}
	}

	private suspend inline fun downloadScripts(manifest: OkkeiManifest) {
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (files.scripts.verify()) {
			return
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_downloading_scripts))
		val scriptsHash: String
		try {
			files.scripts.delete()
			files.scripts.create()
			scriptsHash = httpDownloader.download(
				manifest.patches[Language.English]?.get(
					PatchFile.Scripts.name
				)?.url!!,
				files.scripts.createOutputStream(),
				hashing = true
			) { progressData -> progressPublisher.mutableProgress.emit(progressData) }
		} catch (e: Throwable) {
			throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (scriptsHash != manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.hash) {
			throw OkkeiException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_scripts_hash))
		Preferences.set(FileHashKey.scripts_hash.name, scriptsHash)
		manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.version?.let {
			Preferences.set(FileVersionKey.scripts_version.name, it)
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun extractScripts() = withContext(ioDispatcher) {
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip = ZipFile(files.scripts.fullPath).apply { isRunInThread = true }
		scriptsZip.use {
			with(it) {
				extractAll(extractedScriptsDirectory.absolutePath)
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt(),
						progressMonitor.totalWork.toInt()
					)
					delay(30)
				}
			}
		}
		extractedScriptsDirectory
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun replaceScripts(apkZip: ZipFile, scriptsDirectory: File) {
		withContext(ioDispatcher) {
			with(apkZip) {
				val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
				val scriptsList = scriptsDirectory.listFiles()!!.filter { it.isFile }
				val apkSize = file.length().toInt()
				val scriptsSize = scriptsList.map { it.length() }.sum().toInt()
				val progressMax = scriptsSize + apkSize
				val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
				removeFiles(apkScriptsList)
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt(),
						progressMax
					)
					delay(30)
				}
				addFiles(scriptsList, parameters)
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt() + apkSize,
						progressMax
					)
					delay(30)
				}
				scriptsDirectory.deleteRecursively()
			}
		}
	}
}