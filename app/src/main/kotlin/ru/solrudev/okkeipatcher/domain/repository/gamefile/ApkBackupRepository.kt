package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.Result

interface ApkBackupRepository {
	val backupExists: Boolean
	fun deleteBackup()
	suspend fun createBackup()
	suspend fun verifyBackup(): Boolean
	suspend fun installBackup(): Result
}