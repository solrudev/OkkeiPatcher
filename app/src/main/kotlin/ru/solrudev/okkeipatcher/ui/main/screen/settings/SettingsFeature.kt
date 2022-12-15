package ru.solrudev.okkeipatcher.ui.main.screen.settings

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ObserveHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.OnHandleSaveDataClickMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.reducer.SettingsReducer
import javax.inject.Inject

@ViewModelScoped
class SettingsFeature @Inject constructor(
	observeHandleSaveDataMiddleware: ObserveHandleSaveDataMiddleware,
	onHandleSaveDataClickMiddleware: OnHandleSaveDataClickMiddleware,
	settingsReducer: SettingsReducer
) : BaseFeature<SettingsEvent, SettingsUiState>(
	middlewares = listOf(
		observeHandleSaveDataMiddleware,
		onHandleSaveDataClickMiddleware
	),
	reducer = settingsReducer,
	initialUiState = SettingsUiState()
)