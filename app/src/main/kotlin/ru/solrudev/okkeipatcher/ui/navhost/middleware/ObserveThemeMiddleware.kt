package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.usecase.app.GetThemeFlowUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.ThemeChanged
import javax.inject.Inject

class ObserveThemeMiddleware @Inject constructor(
	private val getThemeFlowUseCase: GetThemeFlowUseCase
) : Middleware<NavHostEvent> {

	override fun apply(events: Flow<NavHostEvent>) = getThemeFlowUseCase().map(::ThemeChanged)
}