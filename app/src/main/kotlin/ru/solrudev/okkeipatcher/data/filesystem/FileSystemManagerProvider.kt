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

package ru.solrudev.okkeipatcher.data.filesystem

import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.topjohnwu.superuser.ipc.RootService
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs
import ru.solrudev.okkeipatcher.app.model.OperationMode
import ru.solrudev.okkeipatcher.app.repository.OperationModeRepository
import ru.solrudev.okkeipatcher.data.service.BaseServiceConnection
import kotlin.system.exitProcess

class FileSystemManagerProvider(
	operationMode: Flow<OperationMode>,
	operationModeRepository: OperationModeRepository,
	ioDispatcher: CoroutineDispatcher,
	private val mainDispatcher: CoroutineDispatcher,
	packageName: String
) : AutoCloseable {

	@Volatile
	private var flowJob: Job? = null

	@Volatile
	private var currentMode = OperationMode.NonRoot

	@Volatile
	private var boundMode = OperationMode.NonRoot

	private val shizukuArgs = UserServiceArgs(ComponentName(packageName, RemoteFileSystemService::class.java.name))
		.daemon(false)
		.processNameSuffix("remotefs")
		.tag("RemoteFileSystemService")
		.version(1)

	private val coroutineScope = CoroutineScope(ioDispatcher)
	private val effectiveOperationMode = operationModeRepository.getEffectiveOperationModeFlow(operationMode)
	private val rootIntent = Intent().setComponent(ComponentName(packageName, RootFileSystemService::class.java.name))
	private val shizukuConnection = RemoteFileSystemConnection()
	private val rootConnection = RemoteFileSystemConnection()

	fun get(): FileSystemManager? {
		if (flowJob != null) {
			return getService()
		}
		synchronized(this) {
			if (flowJob == null) {
				flowJob = effectiveOperationMode.onEach(::onOperationModeChanged).launchIn(coroutineScope)
				runBlocking { effectiveOperationMode.first() }
			}
		}
		return getService()
	}

	override fun close() = coroutineScope.cancel()

	private fun getService(): FileSystemManager? {
		val connection = when (currentMode) {
			OperationMode.Root -> rootConnection
			OperationMode.Shizuku -> shizukuConnection
			OperationMode.NonRoot -> return null
		}
		return runBlocking { connection.awaitService() }
	}

	private fun onOperationModeChanged(mode: OperationMode) {
		currentMode = mode
		if (mode == boundMode) {
			return
		}
		unbind()
		bind(mode)
	}

	@Synchronized
	private fun bind(mode: OperationMode) {
		if (mode == OperationMode.NonRoot || boundMode == mode) {
			return
		}
		try {
			when (mode) {
				OperationMode.Root -> runBlocking {
					withContext(mainDispatcher) {
						RootService.bind(rootIntent, rootConnection)
					}
				}

				OperationMode.Shizuku -> {
					if (Shizuku.getVersion() < 10) {
						return
					}
					Shizuku.bindUserService(shizukuArgs, shizukuConnection)
				}

				OperationMode.NonRoot -> return
			}
			boundMode = mode
		} catch (_: Exception) {
			boundMode = OperationMode.NonRoot
		}
	}

	@Synchronized
	private fun unbind() {
		try {
			when (boundMode) {
				OperationMode.Root -> runBlocking {
					withContext(mainDispatcher) {
						RootService.unbind(rootConnection)
					}
				}

				OperationMode.Shizuku -> {
					if (Shizuku.getVersion() < 10) {
						return
					}
					Shizuku.unbindUserService(shizukuArgs, shizukuConnection, true)
				}

				OperationMode.NonRoot -> { // no-op
				}
			}
		} catch (_: Exception) { // no-op
		} finally {
			boundMode = OperationMode.NonRoot
		}
	}
}

class RemoteFileSystemService : IRemoteFileSystemService.Stub() {
	override fun destroy() = exitProcess(0)
	override fun getFileSystemService() = FileSystemManager.getService()
}

class RootFileSystemService : RootService() {
	override fun onBind(intent: Intent): IBinder = RemoteFileSystemService()
}

private class RemoteFileSystemConnection : BaseServiceConnection<FileSystemManager>() {
	override fun getService(service: IBinder): FileSystemManager? {
		val remoteFileSystemService = IRemoteFileSystemService.Stub.asInterface(service)
		return try {
			FileSystemManager.getRemote(remoteFileSystemService.fileSystemService)
		} catch (_: RemoteException) {
			null
		}
	}
}