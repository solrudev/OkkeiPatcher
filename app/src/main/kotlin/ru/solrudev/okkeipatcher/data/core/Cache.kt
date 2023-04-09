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

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface Cache<out T> {
	suspend fun retrieve(refresh: Boolean = false): T
}

class InMemoryCache<out T>(private val onRefresh: suspend () -> T) : Cache<T> {

	private val mutex = Mutex()
	private var cache: T? = null

	override suspend fun retrieve(refresh: Boolean): T {
		mutex.withLock {
			if (!refresh) {
				cache?.let { return it }
			}
			return onRefresh().also { cache = it }
		}
	}
}