package ru.solrudev.okkeipatcher.domain.usecase.app

import ru.solrudev.okkeipatcher.domain.model.Theme
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class PersistThemeUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	suspend operator fun invoke(theme: Theme) = preferencesRepository.theme.persist(theme)
}