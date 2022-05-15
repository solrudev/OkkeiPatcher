package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.model.Language
import ru.solrudev.okkeipatcher.domain.core.persistence.Dao

interface PreferencesRepository {
	val isPatchedDao: Dao<Boolean>
	val handleSaveDataDao: Dao<Boolean>
	val patchLanguageDao: Dao<Language>
	suspend fun reset()
}