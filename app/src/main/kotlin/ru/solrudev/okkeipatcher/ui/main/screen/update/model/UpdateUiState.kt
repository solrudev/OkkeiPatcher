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

package ru.solrudev.okkeipatcher.ui.main.screen.update.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

data class UpdateUiState(
	val isLoading: Boolean = false,
	val isUpdateButtonEnabled: Boolean = true,
	val isUpdateButtonVisible: Boolean = false,
	val isUpdateAvailable: Boolean = false,
	val state: UpdateState = UpdateState.Idle,
	val buttonText: LocalizedString = LocalizedString.resource(R.string.button_text_update),
	val status: LocalizedString = LocalizedString.resource(R.string.update_status_no_update),
	val progressData: ProgressData = ProgressData(),
	val updateSize: Double = 0.0,
	val changelog: Map<String, List<String>> = emptyMap(),
	val currentWork: Work? = null
) : JetState

sealed interface UpdateState {
	sealed interface Updating
	data object Idle : UpdateState
	data object Downloading : UpdateState, Updating
	data object InstallPending : UpdateState
	data object Installing : UpdateState, Updating
}

val UpdateUiState.isChangelogVisible: Boolean
	get() = isUpdateAvailable || state !is UpdateState.Idle

val UpdateUiState.percentDone: Int
	get() = (progressData.progress.toDouble() / progressData.max * 100).toInt()