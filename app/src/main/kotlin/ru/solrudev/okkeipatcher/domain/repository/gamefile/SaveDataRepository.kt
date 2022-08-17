package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.Result

interface SaveDataRepository {
	val backupExists: Boolean
	fun deleteBackup()
	fun deleteTemp()
	suspend fun createTemp(): Result
	suspend fun verifyBackup(): Boolean
	suspend fun restoreBackup(): Result
	suspend fun persistTempAsBackup()
}