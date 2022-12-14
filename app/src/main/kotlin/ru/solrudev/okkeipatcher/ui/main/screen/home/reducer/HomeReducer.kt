package ru.solrudev.okkeipatcher.ui.main.screen.home.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEvent
import javax.inject.Inject

class HomeReducer @Inject constructor(
	private val patchStatusReducer: PatchStatusReducer,
	private val patchReducer: PatchReducer,
	private val restoreReducer: RestoreReducer
) : Reducer<HomeEvent, HomeUiState> {

	override fun reduce(event: HomeEvent, state: HomeUiState) = when (event) {
		is PatchEvent -> patchReducer.reduce(event, state)
		is RestoreEvent -> restoreReducer.reduce(event, state)
		is PatchStatusChanged -> patchStatusReducer.reduce(event, state)
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