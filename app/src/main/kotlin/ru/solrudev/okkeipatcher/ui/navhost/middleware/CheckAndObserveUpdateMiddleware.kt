package ru.solrudev.okkeipatcher.ui.navhost.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsUpdateAvailableFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.UpdateAvailabilityChanged
import javax.inject.Inject

class CheckAndObserveUpdateMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase,
	private val getIsUpdateAvailableFlowUseCase: GetIsUpdateAvailableFlowUseCase
) : Middleware<NavHostEvent> {

	override fun apply(events: Flow<NavHostEvent>) = flow {
		getUpdateDataUseCase(refresh = true)
		getIsUpdateAvailableFlowUseCase()
			.map(::UpdateAvailabilityChanged)
			.collect(::emit)
	}
}