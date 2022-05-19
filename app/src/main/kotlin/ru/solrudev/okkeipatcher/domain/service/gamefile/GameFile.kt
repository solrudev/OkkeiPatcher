package ru.solrudev.okkeipatcher.domain.service.gamefile

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString

interface GameFile : AutoCloseable {
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