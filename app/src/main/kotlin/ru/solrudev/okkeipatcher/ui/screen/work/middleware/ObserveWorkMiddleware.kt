package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.domain.model.WorkState
import ru.solrudev.okkeipatcher.domain.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.domain.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.StartObservingWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent.*
import javax.inject.Inject

class ObserveWorkMiddleware @Inject constructor(
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	private val completeWorkUseCase: CompleteWorkUseCase
) : Middleware<WorkEvent> {

	override fun apply(events: Flow<WorkEvent>) = channelFlow {
		var shouldCollect = true
		launch {
			events
				.shouldCollect()
				.collect { shouldCollect = it }
		}
		events.collectEvent<StartObservingWork> { event ->
			getWorkStateFlowUseCase(event.work)
				.filter { shouldCollect }
				.collect { workState ->
					if (workState.isFinished) {
						completeWorkUseCase(event.work)
					}
					send(workState.toEvent())
				}
		}
	}

	private fun WorkState.toEvent() = when (this) {
		is WorkState.Running -> Running(status, progressData)
		is WorkState.Failed -> Failed(reason, stackTrace)
		is WorkState.Succeeded -> Succeeded
		is WorkState.Canceled -> Canceled
		is WorkState.Unknown -> Unknown
	}

	private fun Flow<WorkEvent>.shouldCollect() = flow {
		collect {
			if (it is StartObservingWork) emit(true)
			if (it is ViewHidden) emit(false)
		}
	}
}