package ru.solrudev.okkeipatcher.ui.screen.settings.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEffect
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsUiState
import javax.inject.Inject

class SettingsReducer @Inject constructor() : Reducer<SettingsEvent, SettingsUiState> {

	override fun reduce(event: SettingsEvent, state: SettingsUiState) = when (event) {
		is SettingsEffect -> state
		is HandleSaveDataChanged -> state.copy(handleSaveData = event.handleSaveData)
		is SaveDataAccessRequested -> state.copy(requestSaveDataAccess = true)
		is SaveDataAccessRequestHandled -> state.copy(requestSaveDataAccess = false)
	}
}