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

package ru.solrudev.okkeipatcher.ui.navhost.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.Theme
import ru.solrudev.okkeipatcher.app.model.Work

sealed interface NavHostEvent : JetEvent {
	object NavigatedToWorkScreen : NavHostEvent
	object NavigatedToPermissionsScreen : NavHostEvent
	object PermissionsCheckRequested : NavHostEvent, NavHostEffect
	data class PermissionsChecked(val allPermissionsGranted: Boolean) : NavHostEvent
	data class WorkIsPending(val work: Work) : NavHostEvent
	data class ThemeChanged(val theme: Theme) : NavHostEvent
}

sealed interface NavHostEffect : JetEffect