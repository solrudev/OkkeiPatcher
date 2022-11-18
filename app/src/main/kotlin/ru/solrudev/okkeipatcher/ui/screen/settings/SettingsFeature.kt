package ru.solrudev.okkeipatcher.ui.screen.settings

import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.screen.settings.middleware.ObserveHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.settings.middleware.OnHandleSaveDataClickMiddleware
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.reducer.SettingsReducer
import javax.inject.Inject

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