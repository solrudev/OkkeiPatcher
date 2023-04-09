/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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