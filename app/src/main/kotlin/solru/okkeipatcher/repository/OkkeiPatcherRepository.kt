package solru.okkeipatcher.repository

import solru.okkeipatcher.data.OkkeiPatcherChangelog
import solru.okkeipatcher.domain.services.ObservableService
import java.io.File

interface OkkeiPatcherRepository : ObservableService {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getUpdateFile(): File
	suspend fun getChangelog(): OkkeiPatcherChangelog
}