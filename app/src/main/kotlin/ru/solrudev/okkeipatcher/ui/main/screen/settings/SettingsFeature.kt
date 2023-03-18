package ru.solrudev.okkeipatcher.ui.main.screen.settings

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ObserveHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ObserveThemeMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ToggleHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.PersistThemeMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.reducer.SettingsReducer
import javax.inject.Inject

@ViewModelScoped
class SettingsFeature @Inject constructor(
	observeHandleSaveDataMiddleware: ObserveHandleSaveDataMiddleware,
	toggleHandleSaveDataMiddleware: ToggleHandleSaveDataMiddleware,
	observeThemeMiddleware: ObserveThemeMiddleware,
	persistThemeMiddleware: PersistThemeMiddleware,
	settingsReducer: SettingsReducer
) : JetFeature<SettingsEvent, SettingsUiState>(
	middlewares = listOf(
		observeHandleSaveDataMiddleware,
		toggleHandleSaveDataMiddleware,
		observeThemeMiddleware,
		persistThemeMiddleware
	),
	reducer = settingsReducer,
	initialUiState = SettingsUiState()
)