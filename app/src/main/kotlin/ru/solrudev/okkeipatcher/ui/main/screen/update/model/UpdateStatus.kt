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

import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface UpdateStatus {
	object NoUpdate : UpdateStatus
	object UpdateAvailable : UpdateStatus
	data class Downloading(val progressData: ProgressData) : UpdateStatus
	object AwaitingInstallation : UpdateStatus
	object Installing : UpdateStatus
	data class Failed(val reason: LocalizedString) : UpdateStatus
	object Canceled : UpdateStatus
	object Unknown : UpdateStatus
}