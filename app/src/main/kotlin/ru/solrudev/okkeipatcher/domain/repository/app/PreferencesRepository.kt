package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.Language

interface PreferencesRepository {
	val isPatched: Dao<Boolean>
	val handleSaveData: Dao<Boolean>
	val patchLanguage: Dao<Language>
	suspend fun reset()
}