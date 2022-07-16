package ru.solrudev.okkeipatcher.domain.repository.gamefile

interface SaveDataRepository {
	val backupExists: Boolean
	fun deleteBackup()
	fun deleteTemp()
	suspend fun createTemp(): Boolean
	suspend fun verifyBackup(): Boolean
	suspend fun restore(): Boolean
	suspend fun persistTempAsBackup()
}