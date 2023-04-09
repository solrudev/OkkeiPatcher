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

import kotlinx.coroutines.flow.Flow

/**
 * Data access object that allows to retrieve and persist data.
 */
interface Dao<T> : Retrievable<T>, Persistable<T>

/**
 * Data access object that allows to retrieve a single value or a [Flow] of data and persist data.
 */
interface ReactiveDao<T> : Dao<T>, ReactiveRetrievable<T>

/**
 * Data access object that allows to retrieve data.
 */
interface Retrievable<out T> {
	suspend fun retrieve(): T
}

/**
 * Data access object that allows to persist data.
 */
interface Persistable<in T> {
	suspend fun persist(value: T)
	suspend fun clear()
}

/**
 * Data access object that allows to retrieve a [Flow] of data.
 */
interface ReactiveRetrievable<out T> {
	val flow: Flow<T>
}