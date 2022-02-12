package solru.okkeipatcher.domain.services

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.OkkeiStorage
import solru.okkeipatcher.domain.model.files.generic.PatchFileHashKey
import solru.okkeipatcher.domain.services.gamefile.impl.AbstractApk
import solru.okkeipatcher.domain.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.file.VerifiableFile
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.repository.patch.ScriptsDataRepository
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.observe
import solru.okkeipatcher.utils.extensions.reset
import solru.okkeipatcher.utils.extensions.use
import java.io.File

class ScriptsPatcher @AssistedInject constructor(
	@Assisted private val apk: AbstractApk,
	@Assisted private val scriptsDataRepository: ScriptsDataRepository,
	@Assisted private val scriptsFile: VerifiableFile,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val httpDownloader: HttpDownloader
) : ObservableServiceImpl() {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, scriptsFile.progress)

	suspend fun patch() {
		var extractedScriptsDirectory: File? = null
		try {
			downloadScripts()
			extractedScriptsDirectory = extractScripts()
			val apkZip = apk.asZipFile()
			apkZip.use {
				replaceScripts(it, extractedScriptsDirectory)
			}
			apk.removeSignature()
			apk.sign()
		} finally {
			extractedScriptsDirectory?.let { if (it.exists()) it.deleteRecursively() }
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
		mutableStatus.emit(LocalizedString.resource(R.string.status_extracting_scripts))
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip = ZipFile(scriptsFile.fullPath).apply { isRunInThread = true }
		scriptsZip.use {
			with(it) {
				extractAll(extractedScriptsDirectory.absolutePath)
				progressMonitor.observe { progressData ->
					progressPublisher.mutableProgress.emit(progressData)
				}
			}
		}
		extractedScriptsDirectory
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun replaceScripts(apkZip: ZipFile, scriptsDirectory: File) {
		mutableStatus.emit(LocalizedString.resource(R.string.status_replacing_scripts))
		withContext(ioDispatcher) {
			with(apkZip) {
				val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
				val scriptsList = scriptsDirectory.listFiles()!!.filter { it.isFile }
				val apkSize = file.length().toInt()
				val scriptsSize = scriptsList.sumOf { it.length() }.toInt()
				val progressMax = scriptsSize + apkSize
				val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
				removeFiles(apkScriptsList)
				progressMonitor.observe { progressData ->
					progressPublisher.mutableProgress.emit(progressData.progress, progressMax)
				}
				addFiles(scriptsList, parameters)
				progressMonitor.observe { progressData ->
					progressPublisher.mutableProgress.emit(progressData.progress + apkSize, progressMax)
				}
				scriptsDirectory.deleteRecursively()
			}
		}
	}
}