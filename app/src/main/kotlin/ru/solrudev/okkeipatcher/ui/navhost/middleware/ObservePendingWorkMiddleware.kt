package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetPendingWorkFlowUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsChecked
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.WorkIsPending
import javax.inject.Inject

class ObservePendingWorkMiddleware @Inject constructor(
	private val getPendingWorkFlowUseCase: GetPendingWorkFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<NavHostEvent> {

	override fun apply(events: Flow<NavHostEvent>) = channelFlow {
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