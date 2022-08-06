package ru.solrudev.okkeipatcher.ui.host.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.host.model.HostEffect
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent.*
import ru.solrudev.okkeipatcher.ui.host.model.HostUiState
import javax.inject.Inject

class HostReducer @Inject constructor() : Reducer<HostUiState, HostEvent> {

	override fun reduce(state: HostUiState, event: HostEvent) = when (event) {
		is HostEffect -> state
		is NavigatedToWorkScreen -> state.copy(pendingWork = null)
		is NavigatedToPermissionsScreen -> state.copy(permissionsRequired = false)
		is PermissionsChecked -> state.copy(permissionsRequired = !event.allPermissionsGranted)
		is WorkIsPending -> state.copy(pendingWork = event.work)
	}
}