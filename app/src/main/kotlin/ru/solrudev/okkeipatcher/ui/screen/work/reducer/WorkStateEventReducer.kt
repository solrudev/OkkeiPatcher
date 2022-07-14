package ru.solrudev.okkeipatcher.ui.screen.work.reducer

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.plus
import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

class WorkStateEventReducer @Inject constructor() : Reducer<WorkUiState, WorkStateEvent> {

	override fun reduce(state: WorkUiState, event: WorkStateEvent) = when (event) {
		is WorkStateEvent.Running -> state.copy(
			status = event.status,
			progressData = event.progressData
		)
		is WorkStateEvent.Failed -> {
			val newLine = if (event.stackTrace.isNotBlank()) "\n" else ""
			val message = Message(
				LocalizedString.resource(R.string.error),
				event.reason + LocalizedString.raw("$newLine${event.stackTrace}")
			)
			val errorMessage = state.errorMessage.copy(data = message)
			state.copy(errorMessage = errorMessage)
		}
		is WorkStateEvent.Succeeded -> {
			val maxProgress = state.progressData.copy(progress = state.progressData.max)
			state.copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
		is WorkStateEvent.Canceled -> state.copy(isWorkCanceled = true)
		is WorkStateEvent.Unknown -> state
	}
}