package ru.solrudev.okkeipatcher.data.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryCache<out T>(private val onRefresh: suspend () -> T) {

	private val mutex = Mutex()
	private var cache: T? = null

	suspend fun retrieve(refresh: Boolean = false): T {
		mutex.withLock {
			if (!refresh) {
				cache?.let { return it }
			}
			return onRefresh().also { cache = it }
		}
	}
}