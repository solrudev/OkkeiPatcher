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

import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.NotPatched
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.Patched

sealed interface PatchStatus {
	data object Patched : PatchStatus, PersistentPatchStatus
	data object NotPatched : PatchStatus, PersistentPatchStatus
	data object UpdateAvailable : PatchStatus
	data class WorkStarted(val currentStatus: PersistentPatchStatus) : PatchStatus
}

sealed interface PersistentPatchStatus : PatchStatus {
	companion object {
		fun of(value: Boolean) = if (value) Patched else NotPatched
	}
}