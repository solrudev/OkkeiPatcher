package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Work

data class HomeUiState(
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val isPatchSizeLoading: Boolean = false,
	val pendingWork: Work? = null,
	val startPatchMessage: MessageUiState = MessageUiState(),
	val startRestoreMessage: MessageUiState = MessageUiState(),
	val patchUpdatesAvailable: Boolean = false,
	val shouldShowPatchUpdatesMessage: Boolean = false
)