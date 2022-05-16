package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.EnqueuePatchWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkIsPending
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.StartPatch
import javax.inject.Inject

class EnqueuePatchWorkMiddleware @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<StartPatch> {
			val patchWork = enqueuePatchWorkUseCase()
			emit(WorkIsPending(patchWork))
		}
	}
}