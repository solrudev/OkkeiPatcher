package ru.solrudev.okkeipatcher.ui.navhost.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetPendingWorkFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent.PermissionsChecked
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent.WorkIsPending
import javax.inject.Inject

class ObservePendingWorkMiddleware @Inject constructor(
	private val getPendingWorkFlowUseCase: GetPendingWorkFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<HostEvent> {

	override fun apply(events: Flow<HostEvent>) = channelFlow {
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