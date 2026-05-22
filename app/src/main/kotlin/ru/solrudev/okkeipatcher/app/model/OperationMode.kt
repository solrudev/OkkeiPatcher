/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.app.model

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

enum class OperationMode(val value: String, val title: LocalizedString, val isElevated: Boolean) {
	NonRoot("non_root", LocalizedString.resource(R.string.preference_operation_mode_non_root), isElevated = false),
	Root("root", LocalizedString.resource(R.string.preference_operation_mode_root), isElevated = true),
	Shizuku("shizuku", LocalizedString.resource(R.string.preference_operation_mode_shizuku), isElevated = true);

	companion object {
		fun fromValue(value: String?) = entries.find { it.value == value } ?: NonRoot
	}
}