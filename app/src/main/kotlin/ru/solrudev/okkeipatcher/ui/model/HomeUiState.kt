package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Work

data class HomeUiState(
	val pendingWork: Work? = null,
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val patchUpdatesAvailable: Boolean = false,
	val checkedForPatchUpdates: Boolean = false,
	val patchUpdatesMessageShown: Boolean = false,
	val isPatchSizeLoading: Boolean = false,
	val startPatchMessage: MessageUiState = MessageUiState(),
	val startRestoreMessage: MessageUiState = MessageUiState()
)