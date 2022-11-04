package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.EnqueueRestoreWorkUseCase
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchStatus.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.StartRestore
import javax.inject.Inject

class EnqueueRestoreWorkMiddleware @Inject constructor(
	private val enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<StartRestore> {
			enqueueRestoreWorkUseCase()
			val isPatched = getPatchStatusFlowUseCase().first()
			val currentStatus = if (isPatched) Patched else NotPatched
			emit(PatchStatusChanged(WorkStarted(currentStatus)))
		}
	}
}