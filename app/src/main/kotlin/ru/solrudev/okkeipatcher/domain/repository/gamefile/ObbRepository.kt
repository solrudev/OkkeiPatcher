package ru.solrudev.okkeipatcher.domain.repository.gamefile

import okio.Sink
import okio.Source
import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation

interface ObbRepository {
	val obbExists: Boolean
	val backupExists: Boolean
	fun deleteObb()
	fun deleteBackup()
	fun obbSource(): Source
	fun obbSink(): Sink
	fun backup(): ProgressOperation<Unit>
	fun verifyBackup(): ProgressOperation<Boolean>
	fun restore(): ProgressOperation<Unit>
}