package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import javax.inject.Inject

class CancelWorkMiddleware @Inject constructor(
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<WorkEvent> {

	override fun apply(events: Flow<WorkEvent>) = flow<WorkEvent> {
		events.collectEvent<CancelWork> {
			cancelWorkUseCase(it.work)
		}
	}
}