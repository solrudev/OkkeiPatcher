package ru.solrudev.okkeipatcher.app.repository

import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.domain.core.persistence.ReactiveDao
import ru.solrudev.okkeipatcher.domain.repository.PatchStateRepository

interface PreferencesRepository : PatchStateRepository {
	val theme: ReactiveDao<Theme>
	suspend fun reset()
}