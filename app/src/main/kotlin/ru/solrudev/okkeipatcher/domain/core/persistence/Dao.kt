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