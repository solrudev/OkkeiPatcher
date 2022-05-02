package ru.solrudev.okkeipatcher.domain.persistence

interface Dao<T> : Retrievable<T>, Persistable<T>

interface Retrievable<out T> {
	suspend fun retrieve(): T
}

interface Persistable<in T> {
	suspend fun persist(value: T)
}