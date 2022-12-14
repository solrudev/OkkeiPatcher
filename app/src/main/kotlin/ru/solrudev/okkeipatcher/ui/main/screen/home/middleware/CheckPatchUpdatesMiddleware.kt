package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetIsWorkPendingFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.Patched
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.UpdateAvailable
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PersistentPatchStatus
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val getIsWorkPendingFlowUseCase: GetIsWorkPendingFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events
			.filterIsInstance<PatchStatusChanged>()
			.map { event -> event.patchStatus }
			.filterIsInstance<PersistentPatchStatus>()
			.combine(getIsWorkPendingFlowUseCase()) { patchStatus, isWorkPending ->
				!isWorkPending && patchStatus is Patched
			}
			.filter { it }
			.map { getPatchUpdatesUseCase() }
			.filter { it.available }
			.collect { emit(PatchStatusChanged(UpdateAvailable)) }
	}
}