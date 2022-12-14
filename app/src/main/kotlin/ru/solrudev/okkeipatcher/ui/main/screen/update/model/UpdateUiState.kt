package ru.solrudev.okkeipatcher.ui.main.screen.update.model

import io.github.solrudev.jetmvi.JetState

data class UpdateUiState(
	val isLoading: Boolean = false,
	val isUpdateAvailable: Boolean = false,
	val sizeInMb: Double = 0.0,
	val changelog: Map<String, List<String>> = emptyMap()
) : JetState