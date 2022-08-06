package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetIsWorkPendingFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchUpdatesAvailable
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val getIsWorkPendingFlowUseCase: GetIsWorkPendingFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events
			.filterIsInstance<PatchStatusChanged>()
			.filter { event -> event.isPatched }
			.combine(getIsWorkPendingFlowUseCase()) { _, isWorkPending -> isWorkPending }
			.filterNot { it }
			.map { getPatchUpdatesUseCase() }
			.filter { it.available }
			.collect { emit(PatchUpdatesAvailable) }
	}
}