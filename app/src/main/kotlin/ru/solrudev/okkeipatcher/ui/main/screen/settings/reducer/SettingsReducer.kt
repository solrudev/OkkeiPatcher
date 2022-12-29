package ru.solrudev.okkeipatcher.ui.main.screen.settings.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEffect
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import javax.inject.Inject

class SettingsReducer @Inject constructor() : Reducer<SettingsEvent, SettingsUiState> {

	override fun reduce(event: SettingsEvent, state: SettingsUiState) = when (event) {
		is SettingsEffect -> state
		is HandleSaveDataChanged -> state.copy(handleSaveData = event.handleSaveData)
		is SaveDataAccessRequested -> state.copy(requestSaveDataAccess = true)
		is SaveDataAccessRequestHandled -> state.copy(requestSaveDataAccess = false)
		is ThemeChanged -> state.copy(theme = event.theme)
	}
}