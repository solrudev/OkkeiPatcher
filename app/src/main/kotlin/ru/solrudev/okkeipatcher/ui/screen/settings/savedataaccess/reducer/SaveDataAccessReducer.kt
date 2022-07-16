package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEffect
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import javax.inject.Inject

class SaveDataAccessReducer @Inject constructor() : Reducer<SaveDataAccessUiState, SaveDataAccessEvent> {

	override fun reduce(state: SaveDataAccessUiState, event: SaveDataAccessEvent) = when (event) {
		is SaveDataAccessEffect -> state
		is RationaleShown -> {
			val rationale = state.rationale.copy(isVisible = true)
			state.copy(rationale = rationale)
		}
		is RationaleDismissed -> state.copy(rationale = MessageUiState())
		is HandleSaveDataEnabled -> state.copy(handleSaveDataEnabled = true)
		is ViewHidden -> {
			val rationale = state.rationale.copy(isVisible = false)
			state.copy(rationale = rationale)
		}
	}
}