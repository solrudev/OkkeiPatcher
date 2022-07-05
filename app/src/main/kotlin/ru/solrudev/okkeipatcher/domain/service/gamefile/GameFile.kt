package ru.solrudev.okkeipatcher.domain.service.gamefile

import net.lingala.zip4j.ZipFile
import ru.solrudev.okkeipatcher.domain.core.operation.Operation

interface GameFile : AutoCloseable {
	val backupExists: Boolean
	fun deleteBackup()
	fun backup(): Operation<Unit>
	fun restore(): Operation<Unit>
}

interface Patchable {
	fun checkCanPatch()
	fun patch(): Operation<Unit>
	fun update(): Operation<Unit>
}

interface ZipPackage : AutoCloseable {
	suspend fun toZipFile(): ZipFile
	suspend fun sign()
	suspend fun removeSignature()
}

interface PatchableGameFile : GameFile, Patchable