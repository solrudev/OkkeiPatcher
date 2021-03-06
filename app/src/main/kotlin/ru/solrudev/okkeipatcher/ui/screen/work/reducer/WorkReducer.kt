package ru.solrudev.okkeipatcher.ui.screen.work.reducer

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.model.MessageUiState
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEffect
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.*
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

class WorkReducer @Inject constructor(
	private val workStateEventReducer: WorkStateEventReducer
) : Reducer<WorkUiState, WorkEvent> {

	override fun reduce(state: WorkUiState, event: WorkEvent) = when (event) {
		is WorkEffect -> state
		is WorkStateEvent -> workStateEventReducer.reduce(state, event)
		is CancelRequested -> {
			val title = LocalizedString.resource(R.string.warning_abort_title)
			val message = LocalizedString.resource(R.string.warning_abort)
			val cancelMessage = Message(title, message)
			val cancelWorkMessage = state.cancelWorkMessage.copy(data = cancelMessage)
			state.copy(cancelWorkMessage = cancelWorkMessage)
		}
		is CancelMessageShown -> {
			val cancelWorkMessage = state.cancelWorkMessage.copy(isVisible = true)
			state.copy(cancelWorkMessage = cancelWorkMessage)
		}
		is CancelMessageDismissed -> state.copy(cancelWorkMessage = MessageUiState())
		is ErrorShown -> {
			val errorMessage = state.errorMessage.copy(isVisible = true)
			state.copy(errorMessage = errorMessage)
		}
		is ErrorDismissed -> state.copy(errorMessage = MessageUiState())
		is AnimationsPlayed -> state.copy(animationsPlayed = true)
		is ViewHidden -> {
			val cancelWorkMessage = state.cancelWorkMessage.copy(isVisible = false)
			val errorMessage = state.errorMessage.copy(isVisible = false)
			state.copy(
				cancelWorkMessage = cancelWorkMessage,
				errorMessage = errorMessage
			)
		}
	}
}