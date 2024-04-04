/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.ui.main.screen.home.model

import io.github.solrudev.jetmvi.JetState
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.shared.model.MessageUiState

data class HomeUiState(
	val isPatchEnabled: Boolean = true,
	val isRestoreEnabled: Boolean = false,
	val patchStatus: LocalizedString = LocalizedString.resource(R.string.patch_status_not_patched),
	val patchVersion: String = "",
	val isRefreshing: Boolean = false,
	val isPatchSizeLoading: Boolean = false,
	val startPatchMessage: MessageUiState = MessageUiState(),
	val startRestoreMessage: MessageUiState = MessageUiState(),
	val patchUpdatesAvailable: Boolean = false
) : JetState