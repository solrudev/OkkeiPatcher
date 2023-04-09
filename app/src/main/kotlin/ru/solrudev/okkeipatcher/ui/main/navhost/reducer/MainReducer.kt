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

package ru.solrudev.okkeipatcher.ui.main.navhost.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent.UpdateAvailabilityChanged
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import javax.inject.Inject

class MainReducer @Inject constructor() : Reducer<MainEvent, MainUiState> {

	override fun reduce(event: MainEvent, state: MainUiState) = when (event) {
		is UpdateAvailabilityChanged -> state.copy(isUpdateAvailable = event.isUpdateAvailable)
	}
}