package ru.solrudev.okkeipatcher.ui.screen.update.model

import ru.solrudev.okkeipatcher.ui.core.UiState

data class UpdateUiState(
	val isLoading: Boolean = false,
	val isUpdateAvailable: Boolean = false,
	val sizeInMb: Double = 0.0,
	val changelog: Map<String, List<String>> = emptyMap()
) : UiState