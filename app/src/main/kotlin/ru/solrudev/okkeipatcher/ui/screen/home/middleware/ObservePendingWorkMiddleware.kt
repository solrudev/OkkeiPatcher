package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetPendingWorkFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PermissionsChecked
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkIsPending
import javax.inject.Inject

class ObservePendingWorkMiddleware @Inject constructor(
	private val getPendingWorkFlowUseCase: GetPendingWorkFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = channelFlow {
		events
			.filterIsInstance<PermissionsChecked>()
			.collectLatest { event ->
				getPendingWorkFlowUseCase().collect { pendingWork ->
					if (event.allPermissionsGranted) {
						send(WorkIsPending(pendingWork))
					} else {
						cancelWorkUseCase(pendingWork)
					}
				}
			}
	}
}