package ru.solrudev.okkeipatcher.domain.repository.app

import ru.solrudev.okkeipatcher.domain.core.persistence.ReactiveDao
import ru.solrudev.okkeipatcher.domain.model.Language

interface PreferencesRepository {
	val patchStatus: ReactiveDao<Boolean>
	val handleSaveData: ReactiveDao<Boolean>
	val patchLanguage: ReactiveDao<Language>
	val patchVersion: ReactiveDao<String>
	suspend fun reset()
}