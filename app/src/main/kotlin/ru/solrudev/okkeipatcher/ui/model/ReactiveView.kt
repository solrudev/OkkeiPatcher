package ru.solrudev.okkeipatcher.ui.model

interface ReactiveView<in State : UiState> {
	fun render(uiState: State)
}