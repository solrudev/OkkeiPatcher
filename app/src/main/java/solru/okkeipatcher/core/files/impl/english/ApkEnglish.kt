package solru.okkeipatcher.core.files.impl.english

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.R
import solru.okkeipatcher.core.OkkeiStorage
import solru.okkeipatcher.core.files.base.Apk
import solru.okkeipatcher.core.impl.english.FileVersionKey
import solru.okkeipatcher.core.impl.english.PatchFile
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.deleteTempZipFiles
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.model.files.english.FileHashKey
import solru.okkeipatcher.model.files.english.FileInstancesEnglish
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.pm.PackageManager
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.emit
import java.io.File
import javax.inject.Inject

class ApkEnglish @Inject constructor(
	private val fileInstances: FileInstancesEnglish,
	commonFileInstances: CommonFileInstances,
	ioService: IoService
) : Apk(commonFileInstances, ioService) {

	@OptIn(ExperimentalCoroutinesApi::class)
	override val progress = merge(super.progress, fileInstances.scripts.progress)

	override suspend fun patch(manifest: OkkeiManifest) {
		installPatchedIfVerifiedAndBackupExists()
		var extractedScriptsDirectory: File? = null
		tryWrapper(onFinally = {
			deleteTempZipFiles(OkkeiStorage.external)
			extractedScriptsDirectory?.let { if (it.exists()) it.deleteRecursively() }
			commonFileInstances.tempApk.deleteIfExists()
		}) {
			if (!PackageManager.isPackageInstalled(PACKAGE_NAME)) {
				throwErrorMessage(R.string.error_game_not_found)
			}
			copyOriginalApkTo(commonFileInstances.tempApk)
			downloadScripts(manifest)
			statusMutable.emit(R.string.status_extracting_scripts)
			extractedScriptsDirectory = extractScripts()
			statusMutable.emit(R.string.status_replacing_scripts)
			val apkZip =
				ZipFile(commonFileInstances.tempApk.fullPath).apply { isRunInThread = true }
			apkZip.use {
				replaceScripts(it, extractedScriptsDirectory!!)
				removeSignature(it)
			}
			sign()
			installPatched()
		}
	}

	private suspend inline fun downloadScripts(manifest: OkkeiManifest) {
		statusMutable.emit(R.string.status_comparing_scripts)
		if (fileInstances.scripts.verify()) {
			return
		}
		statusMutable.emit(R.string.status_downloading_scripts)
		fileInstances.scripts.downloadAndWrapException(
			manifest.patches[Language.English]?.get(
				PatchFile.Scripts.name
			)?.url!!
		)
		statusMutable.emit(R.string.status_comparing_scripts)
		val scriptsHash = fileInstances.scripts.computeMd5()
		if (scriptsHash != manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.hash) {
			throwErrorMessage(R.string.error_hash_scripts_mismatch)
		}
		statusMutable.emit(R.string.status_writing_scripts_hash)
		Preferences.set(FileHashKey.scripts_hash.name, scriptsHash)
		manifest.patches[Language.English]?.get(PatchFile.Scripts.name)?.version?.let {
			Preferences.set(FileVersionKey.scripts_version.name, it)
		}
	}

	private suspend inline fun extractScripts(): File {
		val extractedScriptsDirectory = File(OkkeiStorage.external, "script")
		val scriptsZip =
			ZipFile(fileInstances.scripts.fullPath).apply { isRunInThread = true }
		scriptsZip.use { zipFile ->
			val scriptsProgressMonitor = zipFile.progressMonitor
			zipFile.extractAll(extractedScriptsDirectory.absolutePath)
			while (scriptsProgressMonitor.state == ProgressMonitor.State.BUSY) {
				progressMutable.emit(
					scriptsProgressMonitor.workCompleted.toInt(),
					scriptsProgressMonitor.totalWork.toInt()
				)
				delay(10)
			}
		}
		return extractedScriptsDirectory
	}

	private suspend inline fun replaceScripts(apkZip: ZipFile, scriptsDirectory: File) {
		val apkProgressMonitor = apkZip.progressMonitor
		val parameters = ZipParameters().apply { rootFolderNameInZip = "assets/script/" }
		val scriptsList = scriptsDirectory.listFiles()!!.filter { it.isFile }
		val apkSize = apkZip.file.length().toInt()
		val scriptsSize = scriptsList.map { it.length() }.sum().toInt()
		val progressMax = scriptsSize + apkSize
		val apkScriptsList = scriptsList.map { "${parameters.rootFolderNameInZip}${it.name}" }
		apkZip.removeFiles(apkScriptsList)
		while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
			progressMutable.emit(
				apkProgressMonitor.workCompleted.toInt(),
				progressMax
			)
			delay(15)
		}
		apkZip.addFiles(scriptsList, parameters)
		while (apkProgressMonitor.state == ProgressMonitor.State.BUSY) {
			progressMutable.emit(
				apkProgressMonitor.workCompleted.toInt() + apkSize,
				progressMax
			)
			delay(15)
		}
		scriptsDirectory.deleteRecursively()
	}
}