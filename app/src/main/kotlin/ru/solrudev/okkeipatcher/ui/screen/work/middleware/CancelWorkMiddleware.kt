package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import javax.inject.Inject

class CancelWorkMiddleware @Inject constructor(
	private val cancelWorkUseCase: CancelWorkUseCase
) : Middleware<WorkEvent> {

	override fun apply(events: Flow<WorkEvent>) = flow<Nothing> {
		events.collectEvent<CancelWork> {
			cancelWorkUseCase(it.work)
		}
	}
}