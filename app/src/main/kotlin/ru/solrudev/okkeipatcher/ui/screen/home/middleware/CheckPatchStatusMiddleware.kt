package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsPatchedUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChecked
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkFinished
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import javax.inject.Inject

class CheckPatchStatusMiddleware @Inject constructor(
	private val getIsPatchedUseCase: GetIsPatchedUseCase
) : Middleware<HomeEvent, HomeUiState> {

	override fun apply(events: Flow<HomeEvent>, state: Flow<HomeUiState>) = flow {

		suspend fun emitPatchStatus() {
			val isPatched = getIsPatchedUseCase()
			emit(PatchStatusChecked(isPatched))
		}

		emitPatchStatus()
		events.collectEvent<WorkFinished> {
			if (it.success) {
				emitPatchStatus()
			}
		}
	}
}