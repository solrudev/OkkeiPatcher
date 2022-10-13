package ru.solrudev.okkeipatcher.ui.screen.home.model

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.core.UiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState

data class HomeUiState(
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val patchStatus: LocalizedString = LocalizedString.resource(R.string.patch_status_not_patched),
	val patchVersion: String = "",
	val isPatchSizeLoading: Boolean = false,
	val startPatchMessage: MessageUiState = MessageUiState(),
	val startRestoreMessage: MessageUiState = MessageUiState(),
	val patchUpdatesAvailable: Boolean = false
) : UiState