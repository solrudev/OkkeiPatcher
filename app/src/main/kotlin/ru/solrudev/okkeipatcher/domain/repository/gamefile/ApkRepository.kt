package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.service.ZipPackage

interface ApkRepository {
	val isInstalled: Boolean
	val tempExists: Boolean
	fun deleteTemp()
	suspend fun createTemp(): ZipPackage
	suspend fun verifyTemp(): Boolean
	suspend fun installTemp(): Result
	suspend fun uninstall(): Boolean
}