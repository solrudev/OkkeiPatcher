package ru.solrudev.okkeipatcher.ui.main.screen.home.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEffect
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEvent.*
import ru.solrudev.okkeipatcher.ui.shared.model.MessageUiState
import javax.inject.Inject

class RestoreReducer @Inject constructor() : Reducer<RestoreEvent, HomeUiState> {

	override fun reduce(event: RestoreEvent, state: HomeUiState) = when (event) {
		is RestoreEffect -> state
		is RestoreRequested -> {
			val title = LocalizedString.resource(R.string.warning_start_restore_title)
			val message = LocalizedString.resource(R.string.warning_abort)
			val startMessage = Message(title, message)
			val startRestoreMessage = state.startRestoreMessage.copy(data = startMessage)
			state.copy(startRestoreMessage = startRestoreMessage)
		}
		is StartRestoreMessageShown -> {
			val startRestoreMessage = state.startRestoreMessage.copy(isVisible = true)
			state.copy(startRestoreMessage = startRestoreMessage)
		}
		is StartRestoreMessageDismissed -> state.copy(startRestoreMessage = MessageUiState())
	}
}