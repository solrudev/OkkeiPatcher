package ru.solrudev.okkeipatcher.ui.model

import ru.solrudev.okkeipatcher.domain.model.Message
import ru.solrudev.okkeipatcher.domain.model.Work

data class HomeUiState(
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val isPatchSizeLoading: Boolean = false,
	val pendingWork: Work? = null,
	val startPatchMessage: Message = Message.empty,
	val startRestoreMessage: Message = Message.empty,
	val patchUpdatesAvailable: Boolean = false,
	val canShowPatchUpdatesMessage: Boolean = false
)