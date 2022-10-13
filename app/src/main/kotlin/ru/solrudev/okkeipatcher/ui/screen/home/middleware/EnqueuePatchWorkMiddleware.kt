package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.EnqueuePatchWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.StartPatch
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchStatus.*
import javax.inject.Inject

class EnqueuePatchWorkMiddleware @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<StartPatch> {
			enqueuePatchWorkUseCase()
			val isPatched = getPatchStatusFlowUseCase().first()
			val currentStatus = if (isPatched) Patched else NotPatched
			emit(PatchStatusChanged(WorkStarted(currentStatus)))
		}
	}
}