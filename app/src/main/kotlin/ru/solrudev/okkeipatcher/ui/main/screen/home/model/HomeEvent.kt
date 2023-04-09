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

package ru.solrudev.okkeipatcher.ui.main.screen.home.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent

sealed interface HomeEvent : JetEvent {
	data class PatchStatusChanged(val patchStatus: PatchStatus) : HomeEvent
	data class PatchVersionChanged(val patchVersion: String) : HomeEvent
	object ViewHidden : HomeEvent
}

sealed interface PatchEvent : HomeEvent {
	data class PatchSizeLoaded(val patchSize: Double) : PatchEvent
	object PatchSizeLoadingStarted : PatchEvent
	object PatchRequested : PatchEvent, PatchEffect
	object PatchUpdatesRequested : PatchEvent, PatchEffect
	object PatchUpdatesLoadingStarted : PatchEvent
	object PatchUpdatesLoaded : PatchEvent
	object StartPatch : PatchEvent, PatchEffect
	object StartPatchMessageShown : PatchEvent
	object StartPatchMessageDismissed : PatchEvent
}

sealed interface RestoreEvent : HomeEvent {
	object RestoreRequested : RestoreEvent
	object StartRestore : RestoreEvent, RestoreEffect
	object StartRestoreMessageShown : RestoreEvent
	object StartRestoreMessageDismissed : RestoreEvent
}

sealed interface PatchEffect : JetEffect
sealed interface RestoreEffect : JetEffect