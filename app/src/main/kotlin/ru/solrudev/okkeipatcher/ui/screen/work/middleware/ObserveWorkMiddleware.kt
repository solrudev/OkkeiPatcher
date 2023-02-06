package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.app.model.WorkState
import ru.solrudev.okkeipatcher.app.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.screen.work.model.ObserveWorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.StartObservingWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent
import javax.inject.Inject

class ObserveWorkMiddleware @Inject constructor(
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	private val completeWorkUseCase: CompleteWorkUseCase
) : Middleware<WorkEvent> {

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun apply(events: Flow<WorkEvent>) = channelFlow {
		events
			.filterIsInstance<ObserveWorkEvent>()
			.flatMapLatest { event ->
				when (event) {
					is StartObservingWork -> event.work.let { work ->
						getWorkStateFlowUseCase(work).map { workState -> work to workState }
					}
					is ViewHidden -> emptyFlow()
				}
			}
			.collect { (work, workState) ->
				if (workState.isFinished) {
					completeWorkUseCase(work)
				}
				send(workState.toEvent())
			}
	}

	private fun WorkState.toEvent() = when (this) {
		is WorkState.Running -> WorkStateEvent.Running(status, progressData)
		is WorkState.Failed -> WorkStateEvent.Failed(reason, stackTrace)
		is WorkState.Succeeded -> WorkStateEvent.Succeeded
		is WorkState.Canceled -> WorkStateEvent.Canceled
		is WorkState.Unknown -> WorkStateEvent.Unknown
	}
}