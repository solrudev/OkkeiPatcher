package ru.solrudev.okkeipatcher.ui.main.navhost.model

import io.github.solrudev.jetmvi.JetState

data class MainUiState(
	val isUpdateAvailable: Boolean = false
) : JetState