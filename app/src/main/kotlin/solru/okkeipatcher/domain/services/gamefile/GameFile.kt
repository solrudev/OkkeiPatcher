package solru.okkeipatcher.domain.services.gamefile

import solru.okkeipatcher.domain.services.ObservableService

interface GameFile : ObservableService {
	val backupExists: Boolean
	fun deleteBackup()
	suspend fun backup()
	suspend fun restore()
	suspend fun verifyBackupIntegrity(): Boolean
}