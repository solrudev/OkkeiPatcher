package solru.okkeipatcher.core.files.base

import solru.okkeipatcher.core.base.ObservableService

interface GameFile : ObservableService {
	val backupExists: Boolean
	fun deleteBackup()
	suspend fun backup()
	suspend fun restore()
	suspend fun verifyBackupIntegrity(): Boolean
}