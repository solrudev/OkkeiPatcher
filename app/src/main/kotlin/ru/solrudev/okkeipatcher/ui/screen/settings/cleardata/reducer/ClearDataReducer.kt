package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEffect
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent.*
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataUiState
import javax.inject.Inject

class ClearDataReducer @Inject constructor() : Reducer<ClearDataUiState, ClearDataEvent> {

	override fun reduce(state: ClearDataUiState, event: ClearDataEvent) = when (event) {
		is ClearDataEffect -> state
		is WarningShown -> {
			val warning = state.warning.copy(isVisible = true)
			state.copy(warning = warning)
		}
		is WarningDismissed -> state.copy(warning = MessageUiState())
		is ClearingFailed -> state.copy(error = event.error)
		is DataCleared -> state.copy(isCleared = true)
		is ErrorMessageShown -> state.copy(canShowErrorMessage = false)
		is ViewHidden -> {
			val warning = state.warning.copy(isVisible = false)
			state.copy(warning = warning)
		}
	}
}