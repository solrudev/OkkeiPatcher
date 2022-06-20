package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.*
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = channelFlow {
		var isWorkPending = false
		launch {
			events
				.isWorkPending()
				.collect { isWorkPending = it }
		}
		events.collectEvent<PatchStatusChanged> {
			// To let the coroutine above collect isWorkPending before proceeding
			yield()
			if (!it.isPatched || isWorkPending) {
				return@collectEvent
			}
			val updatesAvailable = getPatchUpdatesUseCase().available
			if (updatesAvailable) {
				send(PatchUpdatesAvailable)
			}
		}
	}

	private fun Flow<HomeEvent>.isWorkPending() = flow {
		collect {
			if (it is WorkIsPending) emit(true)
			if (it is WorkFinished) emit(false)
		}
	}
}