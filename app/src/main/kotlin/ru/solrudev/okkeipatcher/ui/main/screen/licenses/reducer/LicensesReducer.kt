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

package ru.solrudev.okkeipatcher.ui.main.screen.licenses.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent.LicensesLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesUiState
import javax.inject.Inject

class LicensesReducer @Inject constructor() : Reducer<LicensesEvent, LicensesUiState> {

	override fun reduce(event: LicensesEvent, state: LicensesUiState) = when (event) {
		is LicensesLoaded -> state.copy(licenses = event.licenses)
	}
}