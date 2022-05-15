package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChecked
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchUpdatesAvailable
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase
) : Middleware<HomeEvent, HomeUiState> {

	override fun apply(events: Flow<HomeEvent>, state: Flow<HomeUiState>) = flow {
		events.collectEvent<PatchStatusChecked> {
			if (!it.isPatched) {
				return@collectEvent
			}
			val updatesAvailable = getPatchUpdatesUseCase().available
			if (updatesAvailable) {
				emit(PatchUpdatesAvailable)
			}
		}
	}
}