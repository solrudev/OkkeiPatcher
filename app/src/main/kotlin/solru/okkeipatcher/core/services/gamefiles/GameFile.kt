package solru.okkeipatcher.core.services.gamefiles

import solru.okkeipatcher.core.services.ObservableService

interface GameFile : ObservableService {
	val backupExists: Boolean
	fun deleteBackup()
	suspend fun backup()
	suspend fun restore()
	suspend fun verifyBackupIntegrity(): Boolean
}