package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import javax.inject.Inject

class PersistThemeUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	suspend operator fun invoke(theme: Theme) = preferencesRepository.theme.persist(theme)
}