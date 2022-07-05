package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import java.io.InputStream
import java.io.OutputStream

interface ObbRepository {
	val obbExists: Boolean
	val backupExists: Boolean
	fun deleteObb()
	fun deleteBackup()
	fun openObbInputStream(): InputStream
	fun openObbOutputStream(): OutputStream
	fun backup(): ProgressOperation<Unit>
	fun verifyBackup(): ProgressOperation<Boolean>
	fun restore(): ProgressOperation<Unit>
}