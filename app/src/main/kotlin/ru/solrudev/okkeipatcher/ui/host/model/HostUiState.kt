package ru.solrudev.okkeipatcher.ui.host.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.UiState

data class HostUiState(
	val permissionsRequired: Boolean = false,
	val pendingWork: Work? = null
) : UiState