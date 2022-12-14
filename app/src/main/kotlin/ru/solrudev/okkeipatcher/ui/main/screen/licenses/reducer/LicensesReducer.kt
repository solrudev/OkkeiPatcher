package ru.solrudev.okkeipatcher.ui.main.screen.licenses.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent.LicensesLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesUiState
import javax.inject.Inject

class LicensesReducer @Inject constructor() : Reducer<LicensesEvent, LicensesUiState> {

	override fun reduce(event: LicensesEvent, state: LicensesUiState) = when (event) {
		is LicensesLoaded -> state.copy(licenses = event.licenses)
	}
}