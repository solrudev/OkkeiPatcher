package ru.solrudev.okkeipatcher.ui.screen.licenses.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesUiState
import javax.inject.Inject

class LicensesReducer @Inject constructor() : Reducer<LicensesUiState, LicensesEvent> {

	override fun reduce(state: LicensesUiState, event: LicensesEvent) = when (event) {
		is LicensesEvent.LicensesLoaded -> state.copy(licenses = event.licenses)
	}
}