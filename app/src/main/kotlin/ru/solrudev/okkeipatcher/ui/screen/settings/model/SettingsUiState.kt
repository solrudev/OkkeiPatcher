package ru.solrudev.okkeipatcher.ui.screen.settings.model

import ru.solrudev.okkeipatcher.ui.core.UiState

data class SettingsUiState(
	val handleSaveData: Boolean = true,
	val requestSaveDataAccess: Boolean = false
) : UiState