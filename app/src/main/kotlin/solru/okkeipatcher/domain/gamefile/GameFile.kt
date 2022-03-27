package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.domain.operation.Operation

interface GameFile {
	val backupExists: Boolean
	fun deleteBackup()
	fun backup(): Operation<Unit>
	fun restore(): Operation<Unit>
}