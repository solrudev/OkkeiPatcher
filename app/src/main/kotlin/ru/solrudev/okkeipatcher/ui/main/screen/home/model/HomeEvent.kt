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

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent

sealed interface HomeEvent : JetEvent {
	data class PatchStatusChanged(val patchStatus: PatchStatus) : HomeEvent
	data class PatchVersionChanged(val patchVersion: String) : HomeEvent
	data object ViewHidden : HomeEvent
	data object RefreshRequested : HomeEvent
}

sealed interface PatchEvent : HomeEvent {
	data class PatchSizeLoaded(val patchSize: Double) : PatchEvent
	data object PatchSizeLoadingStarted : PatchEvent
	data object PatchRequested : PatchEvent, PatchEffect
	data object PatchUpdatesLoaded : PatchEvent
	data object StartPatch : PatchEvent, PatchEffect
	data object StartPatchMessageShown : PatchEvent
	data object StartPatchMessageDismissed : PatchEvent
}

sealed interface RestoreEvent : HomeEvent {
	data object RestoreRequested : RestoreEvent
	data object StartRestore : RestoreEvent, RestoreEffect
	data object StartRestoreMessageShown : RestoreEvent
	data object StartRestoreMessageDismissed : RestoreEvent
}

sealed interface PatchEffect : JetEffect
sealed interface RestoreEffect : JetEffect