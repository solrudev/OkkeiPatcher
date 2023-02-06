package ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.app.usecase.GetThemeFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ThemeChanged
import javax.inject.Inject

class ObserveThemeMiddleware @Inject constructor(
	private val getThemeFlowUseCase: GetThemeFlowUseCase
) : Middleware<SettingsEvent> {

	override fun apply(events: Flow<SettingsEvent>) = getThemeFlowUseCase().map(::ThemeChanged)
}