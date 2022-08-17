package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation

interface ObbBackupRepository {
	val backupExists: Boolean
	fun deleteBackup()
	fun createBackup(): ProgressOperation<Unit>
	fun verifyBackup(): ProgressOperation<Boolean>
	fun restoreBackup(): ProgressOperation<Unit>
}