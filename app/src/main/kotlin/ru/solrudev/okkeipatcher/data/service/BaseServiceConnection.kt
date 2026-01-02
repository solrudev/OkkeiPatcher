/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.data.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

private val INITIAL_VALUE = Any()

abstract class BaseServiceConnection<T> : ServiceConnection {

	private val serviceFlow = MutableStateFlow<Any?>(INITIAL_VALUE)

	protected abstract fun getService(service: IBinder): T?

	override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
		if (service == null || !service.pingBinder()) {
			serviceFlow.value = null
			return
		}
		val serviceFromBinder = getService(service)
		serviceFlow.value = serviceFromBinder
	}

	override fun onServiceDisconnected(name: ComponentName?) {
		serviceFlow.value = null
	}

	suspend fun awaitService(): T? {
		@Suppress("UNCHECKED_CAST")
		return serviceFlow
			.filter { it !== INITIAL_VALUE }
			.first() as T?
	}
}