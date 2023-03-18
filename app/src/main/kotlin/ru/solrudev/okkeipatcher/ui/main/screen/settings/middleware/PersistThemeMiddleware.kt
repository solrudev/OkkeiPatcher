package ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.PersistThemeUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.PersistTheme
import javax.inject.Inject

class PersistThemeMiddleware @Inject constructor(
	private val persistThemeUseCase: PersistThemeUseCase
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		onEvent<PersistTheme> { event ->
			persistThemeUseCase(event.theme)
		}
	}
}