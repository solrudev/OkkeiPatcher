package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEffect
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import javax.inject.Inject

class ClearDataReducer @Inject constructor() : Reducer<ClearDataEvent, ClearDataUiState> {

	override fun reduce(event: ClearDataEvent, state: ClearDataUiState) = when (event) {
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