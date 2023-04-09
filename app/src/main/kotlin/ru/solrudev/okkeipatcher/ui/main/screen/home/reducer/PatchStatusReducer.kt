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
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.*
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PersistentPatchStatus
import javax.inject.Inject

class PatchStatusReducer @Inject constructor() : Reducer<PatchStatusChanged, HomeUiState> {

	override fun reduce(event: PatchStatusChanged, state: HomeUiState): HomeUiState = when (event.patchStatus) {
		is PersistentPatchStatus -> reduce(state, event.patchStatus)
		is WorkStarted -> reduce(state, event.patchStatus.currentStatus)
		is UpdateAvailable -> state.copy(
			isPatchEnabled = true,
			patchStatus = LocalizedString.resource(R.string.patch_status_update_available),
			patchUpdatesAvailable = true
		)
	}

	private fun reduce(state: HomeUiState, patchStatus: PersistentPatchStatus) = when (patchStatus) {
		Patched -> state.copy(
			isPatchEnabled = false,
			isRestoreEnabled = true,
			patchStatus = LocalizedString.resource(R.string.patch_status_patched),
			patchUpdatesAvailable = false
		)
		NotPatched -> state.copy(
			isPatchEnabled = true,
			isRestoreEnabled = false,
			patchStatus = LocalizedString.resource(R.string.patch_status_not_patched),
			patchUpdatesAvailable = false
		)
	}
}