package solru.okkeipatcher.domain.gamefile

import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.operation.Operation
import solru.okkeipatcher.util.isPackageInstalled

interface GameFile {
	val backupExists: Boolean
	fun deleteBackup()
	fun backup(): Operation<Unit>
	fun restore(): Operation<Unit>
}

interface Patchable {
	fun canPatch(onNegative: (message: LocalizedString) -> Unit = {}): Boolean
	fun patch(): Operation<Unit>
	fun update(): Operation<Unit>
}

interface PatchableGameFile : GameFile, Patchable

interface Apk : PatchableGameFile, AutoCloseable {
	companion object {
		const val PACKAGE_NAME = "com.mages.chaoschild_jp"
		val isInstalled get() = isPackageInstalled(PACKAGE_NAME)
	}
}

interface SaveData : GameFile, AutoCloseable