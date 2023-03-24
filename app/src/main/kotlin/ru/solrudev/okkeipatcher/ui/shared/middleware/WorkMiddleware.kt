package ru.solrudev.okkeipatcher.ui.shared.middleware

import io.github.solrudev.jetmvi.JetEvent
import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.shared.model.HasWork
import ru.solrudev.okkeipatcher.ui.shared.model.WorkStateEventFactory
import kotlin.reflect.KClass
import kotlin.reflect.safeCast

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
open class WorkMiddleware<E : JetEvent, StartEvent, CancelEvent, PendingWorkEvent : E>(
	private val getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	private val completeWorkUseCase: CompleteWorkUseCase,
	private val cancelWorkUseCase: CancelWorkUseCase,
	private val workStateEventFactory: WorkStateEventFactory<E>,
	private val startEventClass: KClass<out StartEvent>,
	private val cancelEventClass: KClass<out CancelEvent>,
	private val pendingWorkFlowProducer: () -> Flow<Work> = { emptyFlow() },
	private val pendingWorkEventProducer: ((Work) -> PendingWorkEvent)? = null,
) : JetMiddleware<E>
		where StartEvent : E,
			  StartEvent : HasWork,
			  CancelEvent : E,
			  CancelEvent : HasWork {

	override fun MiddlewareScope<E>.apply() {
		onEventLatest(startEventClass) { event ->
			val work = event.work
			getWorkStateFlowUseCase(work).collect { workState ->
				if (workState.isFinished) {
					completeWorkUseCase(work)
				}
				send(workStateEventFactory.fromWorkState(workState))
			}
		}
		onEvent(cancelEventClass) { event ->
			cancelWorkUseCase(event.work)
		}
		launch {
			pendingWorkFlowProducer().collect { work ->
				pendingWorkEventProducer?.invoke(work)?.let { send(it) }
			}
		}
	}

	private fun <E : JetEvent, R : E> MiddlewareScope<E>.onEventLatest(
		eventClass: KClass<out R>, action: suspend (R) -> Unit
	) = launch {
		transform { event -> eventClass.safeCast(event)?.let { emit(it) } }.collectLatest(action)
	}

	private fun <E : JetEvent, R : E> MiddlewareScope<E>.onEvent(
		eventClass: KClass<out R>, collector: FlowCollector<R>
	) = launch {
		transform { event -> eventClass.safeCast(event)?.let { emit(it) } }.collect(collector)
	}
}