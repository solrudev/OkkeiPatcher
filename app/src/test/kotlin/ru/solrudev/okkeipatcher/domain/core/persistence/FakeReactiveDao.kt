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

package ru.solrudev.okkeipatcher.domain.core.persistence

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeReactiveDao<T>(private val defaultValue: T) : ReactiveDao<T> {

	private val valueHolder = MutableStateFlow(defaultValue)

	override val flow = valueHolder.asStateFlow()

	override suspend fun retrieve(): T {
		return valueHolder.value
	}

	override suspend fun persist(value: T) {
		valueHolder.value = value
	}

	override suspend fun clear() {
		valueHolder.value = defaultValue
	}
}