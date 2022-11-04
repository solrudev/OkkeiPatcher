package ru.solrudev.okkeipatcher.ui.screen.settings.model

import io.github.solrudev.jetmvi.UiState

data class SettingsUiState(
	val handleSaveData: Boolean = true,
	val requestSaveDataAccess: Boolean = false
) : UiState