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
	val isUpdating: Boolean = false,
	val isDownloading: Boolean = false,
	val isInstallPending: Boolean = false,
	val isInstalling: Boolean = false,
	val buttonText: LocalizedString = LocalizedString.resource(R.string.button_text_update),
	val status: LocalizedString = LocalizedString.resource(R.string.update_status_no_update),
	val progressData: ProgressData = ProgressData(),
	val percentDone: Int = 0,
	val updateSize: Double = 0.0,
	val changelog: Map<String, List<String>> = emptyMap(),
	val currentWork: Work? = null
) : JetState

val UpdateUiState.isChangelogVisible: Boolean
	get() = isUpdateAvailable || isUpdating || isInstallPending