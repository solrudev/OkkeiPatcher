package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.util.isPackageInstalled

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

		val isInstalled: Boolean
			get() = isPackageInstalled(PACKAGE_NAME)
	}
}

interface SaveData : GameFile, AutoCloseable