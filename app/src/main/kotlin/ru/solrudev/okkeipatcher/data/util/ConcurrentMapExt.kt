/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.util

import android.os.Build
import java.util.concurrent.ConcurrentMap

fun <K, V> ConcurrentMap<K, V>.computeIfAbsentCompat(key: K & Any, computeFunction: (K) -> V & Any): V {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		return computeIfAbsent(key, computeFunction)
	}
	get(key)?.let { return it }
	val value = computeFunction(key)
	return putIfAbsent(key, value) ?: value
}