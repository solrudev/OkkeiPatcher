package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.Language

interface PreferencesRepository {
	val isPatchedDao: Dao<Boolean>
	val handleSaveDataDao: Dao<Boolean>
	val patchLanguageDao: Dao<Language>
	suspend fun reset()
}