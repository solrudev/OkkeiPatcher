package solru.okkeipatcher.core

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import solru.okkeipatcher.R
import solru.okkeipatcher.core.base.ObservableServiceImpl
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.exceptions.OkkeiFatalException
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.io.utils.extensions.copyFile
import solru.okkeipatcher.io.utils.extensions.download
import solru.okkeipatcher.io.utils.extensions.readAllText
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.extensions.makeIndeterminate
import solru.okkeipatcher.utils.extensions.reset
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val MANIFEST_URL =
	"https://raw.githubusercontent.com/ForrrmerBlack/okkei-patcher/master/Manifest.json"

private const val MANIFEST_FILE_NAME = "Manifest.json"
private const val MANIFEST_BACKUP_FILE_NAME = "ManifestBackup.json"

@Singleton
class ManifestRepository @Inject constructor(private val ioService: IoService) : ObservableServiceImpl() {

	val isManifestLoaded: Boolean
		get() = ::manifest.isInitialized && ::manifestJsonString.isInitialized

	private val manifestFile = File(OkkeiStorage.private, MANIFEST_FILE_NAME)
	private val manifestBackupFile = File(OkkeiStorage.private, MANIFEST_BACKUP_FILE_NAME)
	private lateinit var manifest: OkkeiManifest
	private lateinit var manifestJsonString: String

	suspend fun getManifest(): OkkeiManifest {
		if (isManifestLoaded) {
			return manifest
		}
		try {
			progressProvider.mutableProgress.reset()
			mutableStatus.emit(LocalizedString.resource(R.string.status_manifest_downloading))
			val manifestDownloaded = downloadManifest()
			if (!manifestDownloaded) {
				throw OkkeiFatalException(LocalizedString.resource(R.string.error_manifest_corrupted), null)
			}
		} catch (e: Throwable) {
			if (manifestFile.exists()) manifestFile.delete()
			withContext(NonCancellable) {
				if (!restoreManifestBackup()) {
					throw OkkeiFatalException(LocalizedString.resource(R.string.error_manifest_download_failed), e)
				}
				mutableStatus.emit(LocalizedString.resource(R.string.status_manifest_backup_used))
			}
			return manifest
		} finally {
			withContext(NonCancellable) { progressProvider.mutableProgress.reset() }
		}
		mutableStatus.emit(LocalizedString.resource(R.string.status_manifest_download_completed))
		return manifest
	}

	suspend fun getManifestJsonString(): String {
		if (isManifestLoaded) {
			return manifestJsonString
		}
		getManifest()
		return manifestJsonString
	}

	@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
	private suspend inline fun downloadManifest(): Boolean {
		backupManifest()
		try {
			ioService.download(MANIFEST_URL, manifestFile) { progressData ->
				progressProvider.mutableProgress.emit(progressData)
			}
		} catch (e: Throwable) {
			throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), e)
		}
		return try {
			val jsonString = ioService.readAllText(manifestFile)
			progressProvider.mutableProgress.makeIndeterminate()
			manifest = JsonSerializer.decodeFromString(jsonString)
			manifestJsonString = jsonString
			true
		} catch (e: Throwable) {
			false
		}
	}

	private suspend inline fun backupManifest() {
		if (manifestFile.exists()) {
			ioService.copyFile(manifestFile, manifestBackupFile) { progressData ->
				progressProvider.mutableProgress.emit(progressData)
			}
		}
	}

	@OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
	private suspend inline fun restoreManifestBackup(): Boolean {
		if (!manifestBackupFile.exists()) return false
		return try {
			val jsonString = ioService.readAllText(manifestBackupFile)
			manifest = JsonSerializer.decodeFromString(jsonString)
			ioService.copyFile(manifestBackupFile, manifestFile) { progressData ->
				progressProvider.mutableProgress.emit(progressData)
			}
			manifestJsonString = jsonString
			true
		} catch (e: Throwable) {
			false
		} finally {
			manifestBackupFile.delete()
		}
	}
}