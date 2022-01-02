package solru.okkeipatcher.core.services

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.model.files.generic.PatchFileHashKey
import solru.okkeipatcher.core.services.gamefile.impl.Apk
import solru.okkeipatcher.core.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.repository.patch.ScriptsDataRepository
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.deleteTempZipFiles
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.isPackageInstalled
import solru.okkeipatcher.utils.use
import java.io.File

class ScriptsPatcher @AssistedInject constructor(
	@Assisted private val apk: Apk,
	@Assisted private val scriptsDataRepository: ScriptsDataRepository,
	@Assisted private val scriptsFile: VerifiableFile,
	@Assisted private val commonFiles: CommonFiles,
	private val httpDownloader: HttpDownloader,
	private val ioDispatcher: CoroutineDispatcher
) : ObservableServiceImpl() {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, scriptsFile.progress)

	suspend fun patch() {
		var extractedScriptsDirectory: File? = null
		try {
			if (!isPackageInstalled(Apk.PACKAGE_NAME)) {
				throw OkkeiException(LocalizedString.resource(R.string.error_game_not_found))
			}
			apk.copyOriginalApkTo(commonFiles.tempApk)
			downloadScripts()
			mutableStatus.emit(LocalizedString.resource(R.string.status_extracting_scripts))
			extractedScriptsDirectory = extractScripts()
			mutableStatus.emit(LocalizedString.resource(R.string.status_replacing_scripts))
			val apkZip = ZipFile(commonFiles.tempApk.fullPath).apply { isRunInThread = true }
			apkZip.use {
				replaceScripts(it, extractedScriptsDirectory)
				apk.removeSignature(it)
			}
			apk.sign()
		} finally {
			deleteTempZipFiles(OkkeiStorage.external)
			extractedScriptsDirectory?.let { if (it.exists()) it.deleteRecursively() }
			commonFiles.tempApk.delete()
		}
	}

	private suspend inline fun downloadScripts() {
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (scriptsFile.verify()) {
			return
		}
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_downloading_scripts))
		val scriptsData = scriptsDataRepository.getScriptsData()
		val scriptsHash: String
		try {
			scriptsFile.delete()
			scriptsFile.create()
			val outputStream = scriptsFile.createOutputStream()
			scriptsHash = httpDownloader.download(scriptsData.url, outputStream, hashing = true) { progressData ->
				progressPublisher.mutableProgress.emit(progressData)
			}
		} catch (e: Throwable) {
			scriptsFile.delete()
			throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (scriptsHash != scriptsData.hash) {
			throw OkkeiException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_writing_scripts_hash))
		Preferences.set(PatchFileHashKey.scripts_hash.name, scriptsHash)
		Preferences.set(PatchFileVersionKey.scripts_version.name, scriptsData.version)
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun extractScripts() = withContext(ioDispatcher) {
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip = ZipFile(scriptsFile.fullPath).apply { isRunInThread = true }
		scriptsZip.use {
			with(it) {
				extractAll(extractedScriptsDirectory.absolutePath)
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt(),
						progressMonitor.totalWork.toInt()
					)
					delay(20)
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
					delay(20)
				}
				addFiles(scriptsList, parameters)
				while (progressMonitor.state == ProgressMonitor.State.BUSY) {
					ensureActive()
					progressPublisher.mutableProgress.emit(
						progressMonitor.workCompleted.toInt() + apkSize,
						progressMax
					)
					delay(20)
				}
				scriptsDirectory.deleteRecursively()
			}
		}
	}
}