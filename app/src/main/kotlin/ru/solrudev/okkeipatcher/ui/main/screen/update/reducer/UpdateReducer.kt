package ru.solrudev.okkeipatcher.ui.main.screen.update.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEffect
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import javax.inject.Inject

class UpdateReducer @Inject constructor(
	private val updateStatusReducer: UpdateStatusReducer
) : Reducer<UpdateEvent, UpdateUiState> {

	override fun reduce(event: UpdateEvent, state: UpdateUiState) = when (event) {
		is UpdateEffect -> state
		is UpdateDataLoadingStarted -> state.copy(isLoading = true)
		is UpdateDataLoaded -> state.copy(
			isLoading = false,
			updateSize = event.size,
			changelog = event.changelog.associate { it.versionName to it.changes }
		)
		is UpdateStatusChanged -> updateStatusReducer.reduce(event, state)
		is StartObservingDownloadWork -> state.copy(currentWork = event.work)
	}
}