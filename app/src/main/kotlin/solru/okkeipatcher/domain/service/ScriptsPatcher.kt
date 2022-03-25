package solru.okkeipatcher.domain.service

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.english.PatchFileHashKey
import solru.okkeipatcher.domain.gamefile.impl.AbstractApk
import solru.okkeipatcher.domain.gamefile.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.repository.patch.ScriptsDataRepository
import solru.okkeipatcher.domain.util.extension.emit
import solru.okkeipatcher.domain.util.extension.observe
import solru.okkeipatcher.domain.util.extension.reset
import solru.okkeipatcher.domain.util.extension.use
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.util.Preferences
import java.io.File

class ScriptsPatcher @AssistedInject constructor(
	@Assisted private val apk: AbstractApk,
	@Assisted private val scriptsDataRepository: ScriptsDataRepository,
	@Assisted private val scriptsFile: VerifiableFile,
	private val httpDownloader: HttpDownloader
) : ObservableImpl() {

	override val progress = merge(super.progress, scriptsFile.progress)

	suspend fun patch() {
		var extractedScriptsDirectory: File? = null
		try {
			downloadScripts()
			extractedScriptsDirectory = extractScripts()
			apk.asZipFile().use {
				it.replaceScripts(extractedScriptsDirectory)
			}
			apk.removeSignature()
			apk.sign()
		} finally {
			extractedScriptsDirectory?.let { if (it.exists()) it.deleteRecursively() }
			scriptsFile.delete()
		}
	}

	private suspend inline fun downloadScripts() {
		_status.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (scriptsFile.verify()) {
			return
		}
		progressPublisher._progress.reset()
		_status.emit(LocalizedString.resource(R.string.status_downloading_scripts))
		val scriptsData = scriptsDataRepository.getScriptsData()
		val scriptsHash: String
		try {
			scriptsFile.delete()
			scriptsFile.create()
			val outputStream = scriptsFile.createOutputStream()
			scriptsHash = httpDownloader.download(scriptsData.url, outputStream, hashing = true) { progressData ->
				progressPublisher._progress.emit(progressData)
			}
		} catch (t: Throwable) {
			if (t is CancellationException) {
				throw t
			}
			throw LocalizedException(LocalizedString.resource(R.string.error_http_file_download), cause = t)
		}
		_status.emit(LocalizedString.resource(R.string.status_comparing_scripts))
		if (scriptsHash != scriptsData.hash) {
			throw LocalizedException(LocalizedString.resource(R.string.error_hash_scripts_mismatch))
		}
		_status.emit(LocalizedString.resource(R.string.status_writing_scripts_hash))
		Preferences.set(PatchFileHashKey.scripts_hash.name, scriptsHash)
		Preferences.set(PatchFileVersionKey.scripts_version.name, scriptsData.version)
	}

	private suspend inline fun extractScripts(): File {
		_status.emit(LocalizedString.resource(R.string.status_extracting_scripts))
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip = ZipFile(scriptsFile.fullPath).apply { isRunInThread = true }
		scriptsZip.use {
			it.extractAll(extractedScriptsDirectory.absolutePath)
			it.progressMonitor.observe { progressData ->
				progressPublisher._progress.emit(progressData)
			}
		}
		return extractedScriptsDirectory
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun ZipFile.replaceScripts(scriptsDirectory: File) {
		_status.emit(LocalizedString.resource(R.string.status_replacing_scripts))
		val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
		val scriptsList = scriptsDirectory.listFiles()!!.filter { it.isFile }
		val apkSize = file.length().toInt()
		val scriptsSize = scriptsList.sumOf { it.length() }.toInt()
		val progressMax = scriptsSize + apkSize
		val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
		removeFiles(apkScriptsList)
		progressMonitor.observe { progressData ->
			progressPublisher._progress.emit(progressData.progress, progressMax)
		}
		addFiles(scriptsList, parameters)
		progressMonitor.observe { progressData ->
			progressPublisher._progress.emit(progressData.progress + apkSize, progressMax)
		}
		scriptsDirectory.deleteRecursively()
	}
}