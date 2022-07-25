package ru.solrudev.okkeipatcher.domain.repository.gamefile

import ru.solrudev.okkeipatcher.domain.core.Result

interface ApkRepository {
	val isInstalled: Boolean
	val backupApk: ApkFile
	val tempApk: ApkFile
	suspend fun uninstall(): Boolean
}

interface ApkFile {
	val path: String
	val exists: Boolean
	fun delete()
	suspend fun create()
	suspend fun verify(): Boolean
	suspend fun install(): Result
}