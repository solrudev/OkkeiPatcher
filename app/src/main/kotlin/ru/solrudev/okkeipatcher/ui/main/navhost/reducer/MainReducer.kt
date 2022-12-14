package ru.solrudev.okkeipatcher.ui.main.navhost.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent.UpdateAvailabilityChanged
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import javax.inject.Inject

class MainReducer @Inject constructor() : Reducer<MainEvent, MainUiState> {

	override fun reduce(event: MainEvent, state: MainUiState) = when (event) {
		is UpdateAvailabilityChanged -> state.copy(isUpdateAvailable = event.isUpdateAvailable)
	}
}