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