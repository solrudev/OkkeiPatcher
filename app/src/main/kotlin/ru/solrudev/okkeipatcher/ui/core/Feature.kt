package ru.solrudev.okkeipatcher.ui.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * An object which ties [Middlewares][Middleware] and [Reducer] together into a single complete feature of an app.
 *
 * Implements [Flow] of [UiState], so it can be collected to receive UI state updates.
 */
open class Feature<in E : Event, out S : UiState>(
	private val middlewares: List<Middleware<E, S>> = emptyList(),
	private val reducer: Reducer<S, E>,
	initialUiState: S
) : Flow<S> {

	private val events = MutableSharedFlow<E>(
		extraBufferCapacity = 16,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	private val uiState = MutableStateFlow(initialUiState)

	final override suspend fun collect(collector: FlowCollector<S>) = uiState.collect(collector)

	/**
	 * Launches the feature in the given coroutine scope.
	 * @return [Job] of the feature.
	 */
	fun launchIn(scope: CoroutineScope) = scope.launch {
		launch {
			events
				.combine(uiState) { event, state -> reducer.reduce(state, event) }
				.collect(uiState::emit)
		}
		launch {
			middlewares
				.map { it.apply(events, uiState) }
				.merge()
				.collect(events::emit)
		}
	}

	/**
	 * Dispatch an event.
	 */
	fun dispatchEvent(event: E) = events.tryEmit(event)
}