package ru.solrudev.okkeipatcher.ui.main.screen.home.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEffect
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.*
import ru.solrudev.okkeipatcher.ui.shared.model.MessageUiState
import javax.inject.Inject

class PatchReducer @Inject constructor() : Reducer<PatchEvent, HomeUiState> {

	override fun reduce(event: PatchEvent, state: HomeUiState) = when (event) {
		is PatchEffect -> state
		is PatchSizeLoadingStarted -> state.copy(isPatchSizeLoading = true)
		is PatchSizeLoaded -> {
			val title = LocalizedString.resource(R.string.warning_start_patch_title)
			val message = LocalizedString.resource(R.string.warning_start_patch, event.patchSize)
			val startMessage = Message(title, message)
			val startPatchMessage = state.startPatchMessage.copy(data = startMessage)
			state.copy(
				isPatchSizeLoading = false,
				startPatchMessage = startPatchMessage
			)
		}
		is StartPatchMessageShown -> {
			val startPatchMessage = state.startPatchMessage.copy(isVisible = true)
			state.copy(startPatchMessage = startPatchMessage)
		}
		is StartPatchMessageDismissed -> state.copy(startPatchMessage = MessageUiState())
	}
}