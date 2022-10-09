package ru.solrudev.okkeipatcher.ui.navhost.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEffect
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import javax.inject.Inject

class NavHostReducer @Inject constructor() : Reducer<NavHostUiState, HostEvent> {

	override fun reduce(state: NavHostUiState, event: HostEvent) = when (event) {
		is HostEffect -> state
		is NavigatedToWorkScreen -> state.copy(pendingWork = null)
		is NavigatedToPermissionsScreen -> state.copy(permissionsRequired = false)
		is PermissionsChecked -> state.copy(permissionsRequired = !event.allPermissionsGranted)
		is WorkIsPending -> state.copy(pendingWork = event.work)
	}
}