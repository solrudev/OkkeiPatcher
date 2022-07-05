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