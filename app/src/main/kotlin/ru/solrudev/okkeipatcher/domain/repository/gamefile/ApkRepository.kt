package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.Result

interface ApkRepository {
	val isInstalled: Boolean
	val tempPath: String
	val tempExists: Boolean
	fun deleteTemp()
	suspend fun createTemp()
	suspend fun verifyTemp(): Boolean
	suspend fun installTemp(): Result
	suspend fun uninstall(): Boolean
}