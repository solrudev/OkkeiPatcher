package ru.solrudev.okkeipatcher.ui.main.navhost.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsUpdateAvailableFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent.UpdateAvailabilityChanged
import javax.inject.Inject

class CheckAndObserveUpdateMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase,
	private val getIsUpdateAvailableFlowUseCase: GetIsUpdateAvailableFlowUseCase
) : Middleware<MainEvent> {

	override fun apply(events: Flow<MainEvent>) = flow {
		getUpdateDataUseCase(refresh = true)
		getIsUpdateAvailableFlowUseCase()
			.map(::UpdateAvailabilityChanged)
			.collect(::emit)
	}
}