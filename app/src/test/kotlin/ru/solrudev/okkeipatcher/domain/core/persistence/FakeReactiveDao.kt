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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first

class FakeReactiveDao<T> : ReactiveDao<T> {

	private val valueHolder = MutableSharedFlow<T>(replay = 1)

	override val flow = valueHolder.asSharedFlow()

	override suspend fun retrieve(): T {
		return valueHolder.first()
	}

	override suspend fun persist(value: T) {
		valueHolder.emit(value)
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override suspend fun clear() {
		valueHolder.resetReplayCache()
	}
}