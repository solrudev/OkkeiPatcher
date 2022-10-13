package ru.solrudev.okkeipatcher.ui.screen.home.reducer

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchStatus.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.PersistentPatchStatus
import javax.inject.Inject

class PatchStatusReducer @Inject constructor() : Reducer<HomeUiState, PatchStatusChanged> {

	override fun reduce(state: HomeUiState, event: PatchStatusChanged): HomeUiState = when (event.patchStatus) {
		is PersistentPatchStatus -> reduce(state, event.patchStatus)
		is WorkStarted -> reduce(state, event.patchStatus.currentStatus)
		is UpdateAvailable -> state.copy(
			isPatchEnabled = true,
			patchStatus = LocalizedString.resource(R.string.patch_status_update_available),
			patchUpdatesAvailable = true
		)
	}

	private fun reduce(state: HomeUiState, patchStatus: PersistentPatchStatus) = when (patchStatus) {
		Patched -> state.copy(
			isPatchEnabled = false,
			isRestoreEnabled = true,
			patchStatus = LocalizedString.resource(R.string.patch_status_patched),
			patchUpdatesAvailable = false
		)
		NotPatched -> state.copy(
			isPatchEnabled = true,
			isRestoreEnabled = false,
			patchStatus = LocalizedString.resource(R.string.patch_status_not_patched),
			patchUpdatesAvailable = false
		)
	}
}