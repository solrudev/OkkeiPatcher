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

package ru.solrudev.okkeipatcher.data.core

import android.content.Context
import ru.solrudev.okkeipatcher.domain.core.*

/**
 * Resolves string value for a given [context].
 */
fun LocalizedString.resolve(context: Context): CharSequence = when (this) {
	is EmptyString -> ""
	is RawString -> value
	is ResourceString -> context.getString(resourceId, *getArgValues(context, args))
	is CompoundString -> parts.joinToString(separator = "") { it.resolve(context) }
	is QuantityResourceString -> context.resources.getQuantityString(resourceId, quantity, *getArgValues(context, args))
}

private fun getArgValues(context: Context, args: List<Any>): Array<Any> {
	return args.map {
		if (it is LocalizedString) {
			it.resolve(context)
		} else {
			it
		}
	}.toTypedArray()
}