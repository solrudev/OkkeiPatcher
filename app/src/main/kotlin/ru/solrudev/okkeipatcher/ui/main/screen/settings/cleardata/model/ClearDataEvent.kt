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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface ClearDataEvent : JetEvent {
	data object WarningShown : ClearDataEvent
	data object WarningDismissed : ClearDataEvent
	data object ClearingRequested : ClearDataEvent, ClearDataEffect
	data object DataCleared : ClearDataEvent
	data class ClearingFailed(val error: LocalizedString) : ClearDataEvent
	data object ErrorMessageShown : ClearDataEvent
	data object ViewHidden : ClearDataEvent
}

sealed interface ClearDataEffect : JetEffect