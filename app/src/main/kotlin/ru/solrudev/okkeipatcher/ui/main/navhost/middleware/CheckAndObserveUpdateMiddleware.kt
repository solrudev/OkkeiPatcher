package ru.solrudev.okkeipatcher.ui.main.navhost.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.usecase.GetIsUpdateAvailableFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent.UpdateAvailabilityChanged
import javax.inject.Inject

class CheckAndObserveUpdateMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase,
	private val getIsUpdateAvailableFlowUseCase: GetIsUpdateAvailableFlowUseCase
) : JetMiddleware<MainEvent> {

	override fun MiddlewareScope<MainEvent>.apply() {
		launch {
			getUpdateDataUseCase(refresh = true)
			getIsUpdateAvailableFlowUseCase()
				.map(::UpdateAvailabilityChanged)
				.collect(::send)
		}
	}
}