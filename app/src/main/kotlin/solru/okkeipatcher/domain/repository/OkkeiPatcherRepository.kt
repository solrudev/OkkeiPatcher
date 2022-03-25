package solru.okkeipatcher.domain.repository

import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.base.Observable
import java.io.File
import java.util.*

interface OkkeiPatcherRepository : Observable {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getUpdateFile(): File
	suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto
}