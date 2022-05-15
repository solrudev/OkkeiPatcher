package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.domain.usecase.work.GetIsWorkPendingUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetPatchWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetRestoreWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkIsPending
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import javax.inject.Inject

class CheckPendingWorksMiddleware @Inject constructor(
	private val getIsWorkPendingUseCase: GetIsWorkPendingUseCase,
	private val getPatchWorkUseCase: GetPatchWorkUseCase,
	private val getRestoreWorkUseCase: GetRestoreWorkUseCase
) : Middleware<HomeEvent, HomeUiState> {

	override fun apply(events: Flow<HomeEvent>, state: Flow<HomeUiState>) = flow {
		val patchWork = getPatchWorkUseCase()
		val restoreWork = getRestoreWorkUseCase()
		val pendingWork = pendingWorkOrNull(patchWork) ?: pendingWorkOrNull(restoreWork)
		if (pendingWork != null) {
			emit(WorkIsPending(pendingWork))
		}
	}

	/**
	 * Returns [work] if it's pending, `null` otherwise.
	 */
	private suspend fun pendingWorkOrNull(work: Work?) =
		if (work != null && getIsWorkPendingUseCase(work)) {
			work
		} else {
			null
		}
}