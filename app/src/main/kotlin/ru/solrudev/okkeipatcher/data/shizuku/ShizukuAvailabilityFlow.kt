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

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import rikka.shizuku.Shizuku

@Suppress("FunctionName")
fun ShizukuAvailabilityFlow(isShizukuEnabled: Flow<Boolean>) = combine(
	isShizukuEnabled.distinctUntilChanged(),
	isShizukuBinderAvailable()
) { useShizuku, isBinderAvailable ->
	useShizuku
			&& isBinderAvailable
			&& Shizuku.getVersion() >= 10
			&& Shizuku.checkSelfPermission() == PERMISSION_GRANTED
}
	.catch { _ -> emit(false) }
	.distinctUntilChanged()

private fun isShizukuBinderAvailable() = callbackFlow {
	if (Build.VERSION.SDK_INT < 24) {
		send(false)
		return@callbackFlow
	}
	send(Shizuku.pingBinder())
	val receivedListener = Shizuku.OnBinderReceivedListener {
		trySend(true)
	}
	val deadListener = Shizuku.OnBinderDeadListener {
		trySend(false)
	}
	Shizuku.addBinderReceivedListener(receivedListener)
	Shizuku.addBinderDeadListener(deadListener)
	awaitClose {
		Shizuku.removeBinderReceivedListener(receivedListener)
		Shizuku.removeBinderDeadListener(deadListener)
	}
}
