package solru.okkeipatcher.repository

import solru.okkeipatcher.data.manifest.OkkeiManifest
import java.io.File

interface OkkeiPatcherRepository {
	fun isAppUpdateAvailable(manifest: OkkeiManifest): Boolean
	fun appUpdateSizeInMb(manifest: OkkeiManifest): Double
	suspend fun getAppUpdate(manifest: OkkeiManifest): File
}