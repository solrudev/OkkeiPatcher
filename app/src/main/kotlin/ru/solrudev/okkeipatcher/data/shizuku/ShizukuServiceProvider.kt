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

package ru.solrudev.okkeipatcher.data.shizuku

import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import rikka.shizuku.Shizuku
import ru.solrudev.okkeipatcher.data.service.BaseServiceConnection

open class ShizukuServiceProvider<T>(
	private val isShizukuEnabled: Flow<Boolean>,
	ioDispatcher: CoroutineDispatcher,
	private val serviceArgs: Shizuku.UserServiceArgs,
	private val serviceConnection: BaseServiceConnection<T>
) : AutoCloseable {

	private val coroutineScope = CoroutineScope(ioDispatcher)

	@Volatile
	private var flowJob: Job? = null

	@Volatile
	private var isShizukuAvailable = false

	fun get(): T? {
		if (Build.VERSION.SDK_INT < 24) {
			return null
		}
		if (flowJob != null) {
			return getService()
		}
		synchronized(this) {
			if (flowJob == null) {
				val flow = ShizukuAvailabilityFlow(isShizukuEnabled)
				flowJob = flow.onEach(::onShizukuAvailabilityChanged).launchIn(coroutineScope)
				runBlocking { flow.first() }
			}
		}
		return getService()
	}

	override fun close() = coroutineScope.cancel()

	private fun getService(): T? {
		if (!isShizukuAvailable) {
			return null
		}
		return runBlocking {
			serviceConnection.awaitService()
		}
	}

	private fun onShizukuAvailabilityChanged(isShizukuAvailable: Boolean) {
		this.isShizukuAvailable = isShizukuAvailable
		try {
			if (Shizuku.getVersion() < 10) {
				return
			}
			if (isShizukuAvailable) {
				Shizuku.bindUserService(serviceArgs, serviceConnection)
			} else {
				Shizuku.unbindUserService(serviceArgs, serviceConnection, true)
			}
		} catch (_: Exception) { // no-op
		}
	}
}