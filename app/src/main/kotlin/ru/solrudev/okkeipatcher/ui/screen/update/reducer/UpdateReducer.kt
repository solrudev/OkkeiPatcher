package ru.solrudev.okkeipatcher.ui.screen.update.reducer

import ru.solrudev.okkeipatcher.ui.core.Reducer
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEffect
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent.UpdateDataLoaded
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent.UpdateDataLoadingStarted
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateUiState
import javax.inject.Inject

class UpdateReducer @Inject constructor() : Reducer<UpdateEvent, UpdateUiState> {

	override fun reduce(event: UpdateEvent, state: UpdateUiState) = when (event) {
		is UpdateEffect -> state
		is UpdateDataLoadingStarted -> state.copy(isLoading = true)
		is UpdateDataLoaded -> state.copy(
			isLoading = false,
			isUpdateAvailable = event.updateData.isAvailable,
			sizeInMb = event.updateData.sizeInMb,
			changelog = event.updateData.changelog.associate { it.versionName to it.changes }
		)
	}
}