package ru.solrudev.okkeipatcher.domain.usecase.app

import kotlinx.coroutines.flow.distinctUntilChanged
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import javax.inject.Inject

class GetThemeFlowUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {
	operator fun invoke() = preferencesRepository.theme.flow.distinctUntilChanged()
}