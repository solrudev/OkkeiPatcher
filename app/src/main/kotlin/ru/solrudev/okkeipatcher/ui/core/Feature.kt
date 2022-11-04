package ru.solrudev.okkeipatcher.ui.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * An object which represents a single complete feature of an app.
 *
 * Implements [Flow] of [UiState], so it can be collected to receive UI state updates.
 */
sealed interface Feature<in E : Event, out S : UiState> : Flow<S> {

	/**
	 * Launches the feature in the given coroutine scope.
	 * @return [Job] of the feature.
	 */
	fun launchIn(scope: CoroutineScope): Job

	/**
	 * Dispatch an event.
	 */
	fun dispatchEvent(event: E): Boolean
}

/**
 * Standard implementation of [Feature] which assembles [Middlewares][Middleware] and [Reducer] together.
 */
open class AssemblyFeature<E : Event, S : UiState>(
	val middlewares: List<Middleware<E>> = emptyList(),
	val reducer: Reducer<E, S>,
	val initialUiState: S
) : Feature<E, S> {

	private val _events = MutableSharedFlow<E>(
		extraBufferCapacity = 16,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	val events = _events.asSharedFlow()
	private val uiState = MutableStateFlow(initialUiState)

	final override suspend fun collect(collector: FlowCollector<S>) = uiState.collect(collector)

	final override fun launchIn(scope: CoroutineScope) = scope.launch {
		_events
			.combine(uiState, reducer::reduce)
			.onEach(uiState::emit)
			.launchIn(this)
		middlewares
			.map { it.apply(_events) }
			.merge()
			.onEach(_events::emit)
			.launchIn(this)
	}

	final override fun dispatchEvent(event: E) = _events.tryEmit(event)
}