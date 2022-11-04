package ru.solrudev.okkeipatcher.ui.screen.licenses.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesUiState
import javax.inject.Inject

class LicensesReducer @Inject constructor() : Reducer<LicensesEvent, LicensesUiState> {

	override fun reduce(event: LicensesEvent, state: LicensesUiState) = when (event) {
		is LicensesEvent.LicensesLoaded -> state.copy(licenses = event.licenses)
	}
}