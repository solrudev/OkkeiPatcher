package solru.okkeipatcher.core

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.AppServiceBase
import solru.okkeipatcher.core.base.ManifestStrategy
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.extensions.copyFile
import solru.okkeipatcher.io.utils.extensions.readAllText
import solru.okkeipatcher.model.dto.patchupdates.PatchUpdates
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import javax.inject.Inject

private const val MANIFEST_URL =
	"https://raw.githubusercontent.com/ForrrmerBlack/okkei-patcher/master/Manifest.json"

private const val MANIFEST_FILE_NAME = "Manifest.json"
private const val MANIFEST_BACKUP_FILE_NAME = "ManifestBackup.json"

class ManifestRepository @Inject constructor(
	var strategy: ManifestStrategy,
	private val ioService: IoService
) : AppServiceBase() {

	private val manifestFile = File(OkkeiStorage.private, MANIFEST_FILE_NAME)
	private val manifestBackupFile = File(OkkeiStorage.private, MANIFEST_BACKUP_FILE_NAME)
	private lateinit var manifest: OkkeiManifest
	private lateinit var manifestJsonString: String

	val isManifestLoaded: Boolean
		get() = ::manifest.isInitialized && ::manifestJsonString.isInitialized

	val patchUpdates: PatchUpdates
		get() = strategy.patchUpdates(manifest)

	val patchSizeInMb: Double
		get() = strategy.patchSizeInMb(manifest)

	val appUpdateSizeInMb: Double
		get() = "%.2f".format(manifest.okkeiPatcher.size / 1_048_576.0).toDouble()

	suspend fun getManifest(): OkkeiManifest {
		if (isManifestLoaded) {
			return manifest
		}
		try {
			isRunning = true
			progressMutable.reset()
			statusMutable.emit(R.string.status_manifest_downloading)
			val manifestDownloaded = downloadManifest()
			if (!manifestDownloaded) throwFatalErrorMessage(R.string.error_manifest_corrupted)
		} catch (e: Throwable) {
			if (manifestFile.exists()) manifestFile.delete()
			withContext(NonCancellable) {
				if (!restoreManifestBackup()) {
					e.throwFatalErrorMessage(R.string.error_manifest_download_failed)
				}
				statusMutable.emit(R.string.status_manifest_backup_used)
			}
			return manifest
		} finally {
			withContext(NonCancellable) {
				finishTask()
			}
		}
		statusMutable.emit(R.string.status_manifest_download_completed)
		return manifest
	}

	suspend fun getManifestJsonString(): String {
		if (isManifestLoaded) {
			return manifestJsonString
		}
		getManifest()
		return manifestJsonString
	}

	private suspend inline fun downloadManifest(): Boolean {
		backupManifest()
		ioService.downloadAndWrapException(MANIFEST_URL, manifestFile)
		return try {
			val jsonString = ioService.readAllText(manifestFile)
			progressMutable.makeIndeterminate()
			manifest = JsonSerializer.decodeFromString(jsonString)
			manifestJsonString = jsonString
			true
		} catch (e: Throwable) {
			false
		}
	}

	private suspend inline fun backupManifest() {
		if (manifestFile.exists()) {
			ioService.copyFile(manifestFile, manifestBackupFile, progressMutable)
		}
	}

	private suspend inline fun restoreManifestBackup(): Boolean {
		if (!manifestBackupFile.exists()) return false
		return try {
			val jsonString = ioService.readAllText(manifestBackupFile)
			manifest = JsonSerializer.decodeFromString(jsonString)
			ioService.copyFile(manifestBackupFile, manifestFile, progressMutable)
			manifestJsonString = jsonString
			true
		} catch (e: Throwable) {
			false
		} finally {
			manifestBackupFile.delete()
		}
	}
}