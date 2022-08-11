package ru.solrudev.okkeipatcher.domain.repository.gamefile

import okio.Sink

interface ObbRepository {
	val obbExists: Boolean
	fun deleteObb()
	fun obbSink(): Sink
}