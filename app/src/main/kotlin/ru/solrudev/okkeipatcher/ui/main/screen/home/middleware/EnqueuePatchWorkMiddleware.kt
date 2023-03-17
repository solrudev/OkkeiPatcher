package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.EnqueuePatchWorkUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.StartPatch
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.*
import javax.inject.Inject

class EnqueuePatchWorkMiddleware @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : JetMiddleware<HomeEvent> {

	override fun MiddlewareScope<HomeEvent>.apply() {
		onEvent<StartPatch> {
			enqueuePatchWorkUseCase()
			val isPatched = getPatchStatusFlowUseCase().first()
			val currentStatus = if (isPatched) Patched else NotPatched
			send(PatchStatusChanged(WorkStarted(currentStatus)))
		}
	}
}