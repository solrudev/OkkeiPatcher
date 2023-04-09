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

package ru.solrudev.okkeipatcher.ui.util

import android.widget.TextView
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.widget.AbortButton

/**
 * Sets [LocalizedString] value to [TextView].
 */
var TextView.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)
	}

/**
 * Sets [LocalizedString] value to [AbortButton].
 */
var AbortButton.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)?.toString() ?: ""
	}