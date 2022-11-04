package ru.solrudev.okkeipatcher.ui.core

/**
 * Returns new UI state based on current UI state and an event affecting it.
 */
interface Reducer<in E : Event, S : UiState> {
	fun reduce(event: E, state: S): S
}