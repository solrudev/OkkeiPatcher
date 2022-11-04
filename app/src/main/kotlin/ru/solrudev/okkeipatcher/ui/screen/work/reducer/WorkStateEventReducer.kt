package ru.solrudev.okkeipatcher.ui.screen.work.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.EmptyString
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.plus
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent.*
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

class WorkStateEventReducer @Inject constructor() : Reducer<WorkStateEvent, WorkUiState> {

	override fun reduce(event: WorkStateEvent, state: WorkUiState) = when (event) {
		is Running -> state.copy(
			status = event.status,
			progressData = event.progressData
		)
		is Failed -> {
			val newLine = if (event.stackTrace.isNotBlank() && event.reason !is EmptyString) "\n" else ""
			val message = Message(
				LocalizedString.resource(R.string.error),
				event.reason + LocalizedString.raw("$newLine${event.stackTrace}")
			)
			val errorMessage = state.errorMessage.copy(data = message)
			state.copy(errorMessage = errorMessage)
		}
		is Succeeded -> {
			val maxProgress = state.progressData.copy(progress = state.progressData.max)
			state.copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
		is Canceled -> state.copy(isWorkCanceled = true)
		is Unknown -> state
	}
}