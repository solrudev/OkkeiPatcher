package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

class CancelWorkMiddleware @Inject constructor(
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<WorkEvent, WorkUiState> {

	override fun apply(events: Flow<WorkEvent>, state: Flow<WorkUiState>) = flow<WorkEvent> {
		events.collectEvent<CancelWork> {
			cancelWorkUseCase(it.work)
		}
	}
}