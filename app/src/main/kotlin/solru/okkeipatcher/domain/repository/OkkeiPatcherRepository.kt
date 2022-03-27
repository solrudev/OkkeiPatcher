package solru.okkeipatcher.domain.repository

import solru.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import solru.okkeipatcher.domain.operation.Operation
import java.io.File
import java.util.*

interface OkkeiPatcherRepository {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto
	fun getUpdateFile(): Operation<File>
}