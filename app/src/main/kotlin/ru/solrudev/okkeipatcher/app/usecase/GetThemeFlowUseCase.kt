package ru.solrudev.okkeipatcher.app.usecase

import kotlinx.coroutines.flow.distinctUntilChanged
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import javax.inject.Inject

class GetThemeFlowUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	operator fun invoke() = preferencesRepository.theme.flow.distinctUntilChanged()
}