package ru.solrudev.okkeipatcher.ui.screen.home.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent
import javax.inject.Inject

class HomeReducer @Inject constructor(
	private val patchStatusReducer: PatchStatusReducer,
	private val patchReducer: PatchReducer,
	private val restoreReducer: RestoreReducer
) : Reducer<HomeUiState, HomeEvent> {

	override fun reduce(state: HomeUiState, event: HomeEvent) = when (event) {
		is PatchEvent -> patchReducer.reduce(state, event)
		is RestoreEvent -> restoreReducer.reduce(state, event)
		is PatchStatusChanged -> patchStatusReducer.reduce(state, event)
		is PatchVersionChanged -> state.copy(patchVersion = event.patchVersion)
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