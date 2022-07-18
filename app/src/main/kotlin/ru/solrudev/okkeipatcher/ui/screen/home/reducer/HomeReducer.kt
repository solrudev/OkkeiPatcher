package ru.solrudev.okkeipatcher.ui.screen.home.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.home.model.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.*
import javax.inject.Inject

class HomeReducer @Inject constructor(
	private val patchReducer: PatchReducer,
	private val restoreReducer: RestoreReducer
) : Reducer<HomeUiState, HomeEvent> {

	override fun reduce(state: HomeUiState, event: HomeEvent) = when (event) {
		is HomeEffect -> state
		is PatchEvent -> patchReducer.reduce(state, event)
		is RestoreEvent -> restoreReducer.reduce(state, event)
		is PermissionsRequired -> state.copy(permissionsRequired = true)
		is WorkIsPending -> state.copy(pendingWork = event.work)
		is PatchStatusChanged -> state.copy(
			isPatchEnabled = !event.isPatched,
			isRestoreEnabled = event.isPatched,
			canShowPatchUpdatesMessage = event.isPatched
		)
		is PatchUpdatesAvailable -> state.copy(
			isPatchEnabled = true,
			patchUpdatesAvailable = true
		)
		is PatchUpdatesMessageShown -> state.copy(
			patchUpdatesAvailable = false,
			canShowPatchUpdatesMessage = false
		)
		is NavigatedToWorkScreen -> state.copy(
			pendingWork = null,
			patchUpdatesAvailable = false,
			canShowPatchUpdatesMessage = false
		)
		is NavigatedToPermissionsScreen -> state.copy(permissionsRequired = false)
		is ViewHidden -> {
			val startPatchMessage = state.startPatchMessage.copy(isVisible = false)
			val startRestoreMessage = state.startRestoreMessage.copy(isVisible = false)
			state.copy(
				startPatchMessage = startPatchMessage,
				startRestoreMessage = startRestoreMessage
			)
		}
	}
}