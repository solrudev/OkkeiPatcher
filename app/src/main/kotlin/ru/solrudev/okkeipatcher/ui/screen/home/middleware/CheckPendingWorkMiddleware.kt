package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetPendingWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PermissionsChecked
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkIsPending
import javax.inject.Inject

class CheckPendingWorkMiddleware @Inject constructor(
	private val getPendingWorkUseCase: GetPendingWorkUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<PermissionsChecked> { event ->
			val pendingWork = getPendingWorkUseCase() ?: return@collectEvent
			if (event.allPermissionsGranted) {
				emit(WorkIsPending(pendingWork))
			} else {
				cancelWorkUseCase(pendingWork)
			}
		}
	}
}