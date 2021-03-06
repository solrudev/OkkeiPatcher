package ru.solrudev.okkeipatcher.ui.screen.home.model

import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.UiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState

data class HomeUiState(
	val permissionsRequired: Boolean = false,
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val isPatchSizeLoading: Boolean = false,
	val pendingWork: Work? = null,
	val startPatchMessage: MessageUiState = MessageUiState(),
	val startRestoreMessage: MessageUiState = MessageUiState(),
	val patchUpdatesAvailable: Boolean = false,
	val canShowPatchUpdatesMessage: Boolean = true
) : UiState

val HomeUiState.shouldShowPatchUpdatesMessage: Boolean
	get() = patchUpdatesAvailable && canShowPatchUpdatesMessage