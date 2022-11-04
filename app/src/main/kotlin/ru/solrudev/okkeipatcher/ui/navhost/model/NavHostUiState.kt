package ru.solrudev.okkeipatcher.ui.navhost.model

import io.github.solrudev.jetmvi.UiState
import ru.solrudev.okkeipatcher.domain.model.Work

data class NavHostUiState(
	val permissionsRequired: Boolean = false,
	val pendingWork: Work? = null,
	val isUpdateAvailable: Boolean = false
) : UiState