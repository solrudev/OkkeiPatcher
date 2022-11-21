package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao

interface HashRepository {
	val signedApkHash: Dao<String>
	val backupApkHash: Dao<String>
	val backupObbHash: Dao<String>
	val saveDataHash: Dao<String>
	suspend fun clear()
}