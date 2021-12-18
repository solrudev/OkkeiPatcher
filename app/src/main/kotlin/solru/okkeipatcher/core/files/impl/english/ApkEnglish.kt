package solru.okkeipatcher.core.files.impl.english

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.files.base.Apk
import solru.okkeipatcher.core.impl.english.FileVersionKey
import solru.okkeipatcher.core.impl.english.PatchFile
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.deleteTempZipFiles
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.files.common.CommonFiles
import solru.okkeipatcher.model.files.english.FileHashKey
import solru.okkeipatcher.model.files.english.FilesEnglish
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.isPackageInstalled
import java.io.File
import javax.inject.Inject

class ApkEnglish @Inject constructor(
	private val files: FilesEnglish,
	commonFiles: CommonFiles,
	ioService: IoService,
	ioDispatcher: CoroutineDispatcher
) : Apk(commonFiles, ioService, ioDispatcher) {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, files.scripts.progress)

	override suspend fun patch(manifest: OkkeiManifest) {
		progressProvider.mutableProgress.reset()
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
			val apkZip =
				ZipFile(commonFiles.tempApk.fullPath).apply { isRunInThread = true }
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
		try {
			files.scripts.downloadFrom(
				manifest.patches[Language.English]?.get(
					PatchFile.Scripts.name
				)?.url!!
			)
		} catch (e: Throwable) {
			throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), e)
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		val scriptsHash = files.scripts.computeHash()
		if (scriptsHash != manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.hash) {
			throw OkkeiException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_scripts_hash))
		Preferences.set(FileHashKey.scripts_hash.name, scriptsHash)
		manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.version?.let {
			Preferences.set(FileVersionKey.scripts_version.name, it)
		}
	}

	private suspend inline fun extractScripts() = withContext(ioDispatcher) {
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip = ZipFile(files.scripts.fullPath).apply { isRunInThread = true }
		scriptsZip.use { zipFile ->
			val scriptsProgressMonitor = zipFile.progressMonitor
			zipFile.extractAll(extractedScriptsDirectory.absolutePath)
			while (scriptsProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressProvider.mutableProgress.emit(
					scriptsProgressMonitor.workCompleted.toInt(),
					scriptsProgressMonitor.totalWork.toInt()
				)
				delay(30)
			}
		}
		extractedScriptsDirectory
	}

	private suspend inline fun replaceScripts(apkZip: ZipFile, scriptsDirectory: File) {
		withContext(ioDispatcher) {
			val apkProgressMonitor = apkZip.progressMonitor
			val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
			val scriptsList = scriptsDirectory.listFiles()!!.filter { it.isFile }
			val apkSize = apkZip.file.length().toInt()
			val scriptsSize = scriptsList.map { it.length() }.sum().toInt()
			val progressMax = scriptsSize + apkSize
			val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
			apkZip.removeFiles(apkScriptsList)
			while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressProvider.mutableProgress.emit(
					apkProgressMonitor.workCompleted.toInt(),
					progressMax
				)
				delay(30)
			}
			apkZip.addFiles(scriptsList, parameters)
			while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressProvider.mutableProgress.emit(
					apkProgressMonitor.workCompleted.toInt() + apkSize,
					progressMax
				)
				delay(30)
			}
			scriptsDirectory.deleteRecursively()
		}
	}
}