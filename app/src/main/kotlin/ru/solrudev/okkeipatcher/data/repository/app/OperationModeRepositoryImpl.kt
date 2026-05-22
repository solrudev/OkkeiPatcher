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

package ru.solrudev.okkeipatcher.data.repository.app

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runInterruptible
import rikka.shizuku.Shizuku
import ru.solrudev.okkeipatcher.app.model.OperationMode
import ru.solrudev.okkeipatcher.app.repository.OperationModeRepository
import ru.solrudev.okkeipatcher.data.EffectiveOperationModeFlow
import ru.solrudev.okkeipatcher.di.IoDispatcher
import javax.inject.Inject

class OperationModeRepositoryImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : OperationModeRepository {

	override fun getAvailableOperationModes() = buildSet {
		add(OperationMode.NonRoot)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			add(OperationMode.Root)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			add(OperationMode.Shizuku)
		}
	}

	override fun isOperationModeSupported(operationMode: OperationMode) = when (operationMode) {
		OperationMode.NonRoot -> true
		OperationMode.Root -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
		OperationMode.Shizuku -> Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
	}

	override suspend fun isOperationModePermissionGranted(operationMode: OperationMode) = when (operationMode) {
		OperationMode.NonRoot -> true
		OperationMode.Root -> isOperationModeSupported(operationMode) && ensureRootShellAvailable()
		OperationMode.Shizuku -> isOperationModeSupported(operationMode) && try {
			Shizuku.checkSelfPermission() == PERMISSION_GRANTED
		} catch (_: Exception) {
			false
		}
	}

	override fun isShizukuServiceRunning() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Shizuku.pingBinder()

	override fun getEffectiveOperationModeFlow(
		operationMode: Flow<OperationMode>
	) = EffectiveOperationModeFlow(operationMode)

	private suspend fun ensureRootShellAvailable() = runInterruptible(ioDispatcher) {
		try {
			val isRoot = Shell.getShell().isRoot
			if (!isRoot) {
				Shell.getCachedShell()?.close()
			}
			isRoot
		} catch (_: Exception) {
			false
		}
	}
}