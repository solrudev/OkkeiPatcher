package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetIsWorkPendingFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.Patched
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.UpdateAvailable
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PersistentPatchStatus
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val getIsWorkPendingFlowUseCase: GetIsWorkPendingFlowUseCase
) : JetMiddleware<HomeEvent> {

	override fun MiddlewareScope<HomeEvent>.apply() {
		var canLoadPatchUpdates = false
		filterIsInstance<PatchStatusChanged>()
			.map { event -> event.patchStatus }
			.filterIsInstance<PersistentPatchStatus>()
			.combine(getIsWorkPendingFlowUseCase()) { patchStatus, isWorkPending ->
				val canLoadUpdates = !isWorkPending && patchStatus is Patched
				canLoadPatchUpdates = canLoadUpdates
				return@combine canLoadUpdates
			}
			.filter { it }
			.map { getPatchUpdatesUseCase(refresh = true) }
			.filter { it.available }
			.onEach { send(PatchStatusChanged(UpdateAvailable)) }
			.launchIn(this)
		onEvent<PatchUpdatesRequested> {
			send(PatchUpdatesLoadingStarted)
			if (canLoadPatchUpdates && getPatchUpdatesUseCase(refresh = true).available) {
				send(PatchStatusChanged(UpdateAvailable))
			}
			send(PatchUpdatesLoaded)
		}
	}
}