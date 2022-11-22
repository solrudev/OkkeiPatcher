package ru.solrudev.okkeipatcher.domain.repository.gamefile

import okio.Path

interface ObbRepository {
	val obbExists: Boolean
	val obbPath: Path
	fun deleteObb()
}