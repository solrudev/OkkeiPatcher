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
		is WorkIsPending -> state.copy(pendingWork = event.work)
		is PatchStatusChecked -> state.copy(
			isPatchEnabled = !event.isPatched,
			isRestoreEnabled = event.isPatched,
			canShowPatchUpdatesMessage = true
		)
		is PatchUpdatesAvailable -> state.copy(
			isPatchEnabled = true,
			patchUpdatesAvailable = true
		)
		is PatchUpdatesMessageShown -> state.copy(canShowPatchUpdatesMessage = false)
		is NavigatedToWorkScreen -> state.copy(
			pendingWork = null,
			canShowPatchUpdatesMessage = false
		)
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