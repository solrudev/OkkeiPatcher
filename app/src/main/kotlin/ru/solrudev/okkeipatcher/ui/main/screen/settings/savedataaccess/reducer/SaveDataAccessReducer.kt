package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEffect
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import javax.inject.Inject

class SaveDataAccessReducer @Inject constructor() : Reducer<SaveDataAccessEvent, SaveDataAccessUiState> {

	override fun reduce(event: SaveDataAccessEvent, state: SaveDataAccessUiState) = when (event) {
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