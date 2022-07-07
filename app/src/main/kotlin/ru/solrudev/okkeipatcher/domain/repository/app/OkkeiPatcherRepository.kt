package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.data.network.model.OkkeiPatcherChangelogDto
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import java.io.File
import java.util.*

// TODO
interface OkkeiPatcherRepository {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getChangelog(locale: Locale): OkkeiPatcherChangelogDto
	fun getUpdateFile(): Operation<File>
}