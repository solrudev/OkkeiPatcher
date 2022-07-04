package ru.solrudev.okkeipatcher.domain.repository.gamefile

import java.io.File

interface ApkRepository {
	val isInstalled: Boolean
	val backupApk: ApkFile
	val tempApk: ApkFile
	suspend fun uninstall(): Boolean
}

interface ApkFile {
	val file: File
	val exists: Boolean
	fun delete()
	suspend fun create()
	suspend fun verify(): Boolean
}