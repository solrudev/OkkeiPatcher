package ru.solrudev.okkeipatcher.ui.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filterIsInstance

/**
 * Allows to intercept events from a [Feature], perform side effects and emit additional events.
 */
interface Middleware<E : Event> {
	fun apply(events: Flow<E>): Flow<E>
}

/**
 * Starts events flow collection and emits only specified event into a [collector].
 */
suspend inline fun <reified E : Event> Flow<Event>.collectEvent(collector: FlowCollector<E>) {
	filterIsInstance<E>().collect(collector)
}