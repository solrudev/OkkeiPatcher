package solru.okkeipatcher.repository

import solru.okkeipatcher.core.services.ObservableService
import solru.okkeipatcher.data.OkkeiPatcherChangelog
import java.io.File

interface OkkeiPatcherRepository : ObservableService {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getUpdateFile(): File
	suspend fun getChangelog(): OkkeiPatcherChangelog
}