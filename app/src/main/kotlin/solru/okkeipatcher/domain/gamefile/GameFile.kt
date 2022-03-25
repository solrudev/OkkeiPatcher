package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.domain.base.Observable

interface GameFile : Observable {
	val backupExists: Boolean
	fun deleteBackup()
	suspend fun backup()
	suspend fun restore()
	suspend fun verifyBackupIntegrity(): Boolean
}