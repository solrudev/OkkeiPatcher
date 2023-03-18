package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetPendingWorkFlowUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsChecked
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.WorkIsPending
import javax.inject.Inject

class ObservePendingWorkMiddleware @Inject constructor(
	private val getPendingWorkFlowUseCase: GetPendingWorkFlowUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase
) : JetMiddleware<NavHostEvent> {

	override fun MiddlewareScope<NavHostEvent>.apply() {
		onEventLatest<PermissionsChecked> { event ->
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