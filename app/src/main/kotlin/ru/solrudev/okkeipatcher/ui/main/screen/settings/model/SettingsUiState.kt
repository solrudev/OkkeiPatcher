package ru.solrudev.okkeipatcher.ui.main.screen.settings.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.app.model.Theme

data class SettingsUiState(
	val handleSaveData: Boolean = true,
	val requestSaveDataAccess: Boolean = false,
	val theme: Theme = Theme.FollowSystem
) : JetState