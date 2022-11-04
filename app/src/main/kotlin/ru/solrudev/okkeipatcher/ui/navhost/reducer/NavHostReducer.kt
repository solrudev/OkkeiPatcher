package ru.solrudev.okkeipatcher.ui.navhost.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEffect
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import javax.inject.Inject

class NavHostReducer @Inject constructor() : Reducer<NavHostEvent, NavHostUiState> {

	override fun reduce(event: NavHostEvent, state: NavHostUiState) = when (event) {
		is NavHostEffect -> state
		is NavigatedToWorkScreen -> state.copy(pendingWork = null)
		is NavigatedToPermissionsScreen -> state.copy(permissionsRequired = false)
		is PermissionsChecked -> state.copy(permissionsRequired = !event.allPermissionsGranted)
		is WorkIsPending -> state.copy(pendingWork = event.work)
		is UpdateAvailabilityChanged -> state.copy(isUpdateAvailable = event.isUpdateAvailable)
	}
}