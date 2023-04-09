/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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