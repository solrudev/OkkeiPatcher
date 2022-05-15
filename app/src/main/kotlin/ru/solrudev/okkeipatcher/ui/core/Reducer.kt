package ru.solrudev.okkeipatcher.ui.core

/**
 * Returns new UI state based on current UI state and an event affecting it.
 */
interface Reducer<S : UiState, in E : Event> {
	fun reduce(state: S, event: E): S
}