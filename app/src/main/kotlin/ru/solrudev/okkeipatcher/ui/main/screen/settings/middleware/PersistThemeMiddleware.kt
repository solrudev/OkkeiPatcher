package ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.PersistThemeUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.PersistTheme
import javax.inject.Inject

class PersistThemeMiddleware @Inject constructor(
	private val persistThemeUseCase: PersistThemeUseCase
) : Middleware<SettingsEvent> {

	override fun apply(events: Flow<SettingsEvent>) = flow<Nothing> {
		events.collectEvent<PersistTheme> { event ->
			persistThemeUseCase(event.theme)
		}
	}
}