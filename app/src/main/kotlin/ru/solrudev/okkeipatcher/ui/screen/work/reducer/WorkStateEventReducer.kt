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

package ru.solrudev.okkeipatcher.ui.screen.work.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.isEmpty
import ru.solrudev.okkeipatcher.domain.core.plus
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEvent.*
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

class WorkStateEventReducer @Inject constructor() : Reducer<WorkStateEvent, WorkUiState> {

	override fun reduce(event: WorkStateEvent, state: WorkUiState) = when (event) {
		is Running -> state.copy(
			status = event.status,
			progressData = event.progressData
		)
		is Failed -> {
			val newLine = if (event.stackTrace.isNotBlank() && !event.reason.isEmpty()) "\n" else ""
			val message = Message(
				LocalizedString.resource(R.string.error),
				event.reason + LocalizedString.raw("$newLine${event.stackTrace}")
			)
			val errorMessage = state.errorMessage.copy(data = message)
			state.copy(errorMessage = errorMessage)
		}
		is Succeeded -> {
			val maxProgress = state.progressData.copy(progress = state.progressData.max)
			state.copy(
				status = LocalizedString.resource(R.string.status_succeeded),
				progressData = maxProgress,
				isWorkSuccessful = true
			)
		}
		is Canceled -> state.copy(isWorkCanceled = true)
		is Unknown -> state
	}
}