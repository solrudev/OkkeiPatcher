package ru.solrudev.okkeipatcher.ui.navhost.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.UiState

data class NavHostUiState(
	val permissionsRequired: Boolean = false,
	val pendingWork: Work? = null
) : UiState