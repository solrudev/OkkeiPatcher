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
		is PatchUpdatesLoadingStarted -> state.copy(isPatchUpdateLoading = true)
		is PatchUpdatesLoaded -> state.copy(isPatchUpdateLoading = false)
	}
}