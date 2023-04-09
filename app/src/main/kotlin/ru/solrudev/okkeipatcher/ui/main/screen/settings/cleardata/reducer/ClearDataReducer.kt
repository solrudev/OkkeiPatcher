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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEffect
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.shared.model.MessageUiState
import javax.inject.Inject

class ClearDataReducer @Inject constructor() : Reducer<ClearDataEvent, ClearDataUiState> {

	override fun reduce(event: ClearDataEvent, state: ClearDataUiState) = when (event) {
		is ClearDataEffect -> state
		is WarningShown -> {
			val warning = state.warning.copy(isVisible = true)
			state.copy(warning = warning)
		}
		is WarningDismissed -> state.copy(warning = MessageUiState())
		is ClearingFailed -> state.copy(error = event.error)
		is DataCleared -> state.copy(isCleared = true)
		is ErrorMessageShown -> state.copy(canShowErrorMessage = false)
		is ViewHidden -> {
			val warning = state.warning.copy(isVisible = false)
			state.copy(warning = warning)
		}
	}
}