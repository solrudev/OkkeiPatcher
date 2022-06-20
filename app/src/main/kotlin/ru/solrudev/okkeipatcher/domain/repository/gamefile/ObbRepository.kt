package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import java.io.File

interface ObbRepository {
	val backupExists: Boolean
	val obbFile: File
	fun deleteBackup()
	fun backup(): ProgressOperation<Unit>
	fun verifyBackup(): ProgressOperation<Boolean>
	fun restore(): ProgressOperation<Unit>
}