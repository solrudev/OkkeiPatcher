package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.model.OkkeiPatcherVersion
import java.io.File

// TODO
interface OkkeiPatcherRepository {
	suspend fun isUpdateAvailable(): Boolean
	suspend fun getUpdateSizeInMb(): Double
	suspend fun getChangelog(): List<OkkeiPatcherVersion>
	fun getUpdateFile(): Operation<File>
}