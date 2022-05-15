package ru.solrudev.okkeipatcher.domain.core.factory

/**
 * Asynchronous factory which suspends while constructing an object.
 */
interface SuspendFactory<out T> {
	suspend fun create(): T
}