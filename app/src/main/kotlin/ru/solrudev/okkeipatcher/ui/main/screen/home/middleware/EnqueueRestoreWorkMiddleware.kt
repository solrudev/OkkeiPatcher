package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.EnqueueRestoreWorkUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.*
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEvent.StartRestore
import javax.inject.Inject

class EnqueueRestoreWorkMiddleware @Inject constructor(
	private val enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : JetMiddleware<HomeEvent> {

	override fun MiddlewareScope<HomeEvent>.apply() {
		onEvent<StartRestore> {
			enqueueRestoreWorkUseCase()
			val isPatched = getPatchStatusFlowUseCase().first()
			val currentStatus = if (isPatched) Patched else NotPatched
			send(PatchStatusChanged(WorkStarted(currentStatus)))
		}
	}
}