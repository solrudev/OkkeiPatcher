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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEffect
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.shared.model.MessageUiState
import javax.inject.Inject

class SaveDataAccessReducer @Inject constructor() : Reducer<SaveDataAccessEvent, SaveDataAccessUiState> {

	override fun reduce(event: SaveDataAccessEvent, state: SaveDataAccessUiState) = when (event) {
		is SaveDataAccessEffect -> state
		is RationaleShown -> {
			val rationale = state.rationale.copy(isVisible = true)
			state.copy(rationale = rationale)
		}
		is RationaleDismissed -> state.copy(rationale = MessageUiState())
		is HandleSaveDataEnabled -> state.copy(handleSaveDataEnabled = true)
		is ViewHidden -> {
			val rationale = state.rationale.copy(isVisible = false)
			state.copy(rationale = rationale)
		}
	}
}