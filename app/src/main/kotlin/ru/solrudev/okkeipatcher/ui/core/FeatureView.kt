package ru.solrudev.okkeipatcher.ui.core

/**
 * A view which can render UI state object.
 */
interface FeatureView<in S : UiState> {
	fun render(uiState: S) {}
}