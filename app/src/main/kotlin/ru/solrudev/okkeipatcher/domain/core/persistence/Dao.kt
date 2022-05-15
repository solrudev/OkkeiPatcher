package ru.solrudev.okkeipatcher.domain.core.persistence

/**
 * Data access object that allows to retrieve and persist data.
 */
interface Dao<T> : Retrievable<T>, Persistable<T>

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
}