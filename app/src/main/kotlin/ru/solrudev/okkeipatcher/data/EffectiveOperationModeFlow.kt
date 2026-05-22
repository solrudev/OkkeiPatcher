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

package ru.solrudev.okkeipatcher.data

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import rikka.shizuku.Shizuku
import ru.solrudev.okkeipatcher.app.model.OperationMode
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

@Suppress("FunctionName")
fun EffectiveOperationModeFlow(operationMode: Flow<OperationMode>) = combine(
	operationMode.distinctUntilChanged(),
	isShizukuBinderAvailable(),
	isRootAvailable()
) { mode, isShizukuBinderAvailable, isRootAvailable ->
	when (mode) {
		OperationMode.NonRoot -> OperationMode.NonRoot
		OperationMode.Root -> if (isRootAvailable) OperationMode.Root else OperationMode.NonRoot
		OperationMode.Shizuku -> if (
			isShizukuBinderAvailable
			&& Shizuku.getVersion() >= 10
			&& Shizuku.checkSelfPermission() == PERMISSION_GRANTED
		) {
			OperationMode.Shizuku
		} else {
			OperationMode.NonRoot
		}
	}
}
	.catch { _ -> emit(OperationMode.NonRoot) }
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

private fun isRootAvailable() = callbackFlow {
	if (Build.VERSION.SDK_INT < 21) {
		send(false)
		return@callbackFlow
	}
	val callback: (Shell) -> Unit = { shell ->
		trySend(shell.isRoot)
	}
	ShellInitializer.initCallbacks += callback
	val cachedShell = Shell.getCachedShell()
	if (cachedShell != null) {
		send(cachedShell.isRoot)
	} else {
		send(false)
	}
	awaitClose {
		ShellInitializer.initCallbacks -= callback
	}
}

fun initializeRootShellBuilder() {
	val builder = Shell.Builder.create().setInitializers(ShellInitializer::class.java)
	Shell.setDefaultBuilder(builder)
}

private class ShellInitializer : Shell.Initializer() {

	override fun onInit(context: Context, shell: Shell): Boolean {
		for (callback in initCallbacks) {
			callback(shell)
		}
		return true
	}

	companion object {
		val initCallbacks: MutableSet<((Shell) -> Unit)> = Collections.newSetFromMap(ConcurrentHashMap())
	}
}