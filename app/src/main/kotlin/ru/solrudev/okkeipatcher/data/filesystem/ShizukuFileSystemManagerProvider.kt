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
import android.os.IBinder
import android.os.RemoteException
import com.topjohnwu.superuser.nio.FileSystemManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import rikka.shizuku.Shizuku.UserServiceArgs
import ru.solrudev.okkeipatcher.data.service.BaseServiceConnection
import ru.solrudev.okkeipatcher.data.shizuku.ShizukuServiceProvider
import kotlin.system.exitProcess

class ShizukuFileSystemManagerProvider(
	isShizukuEnabled: Flow<Boolean>,
	ioDispatcher: CoroutineDispatcher,
	packageName: String
) : ShizukuServiceProvider<FileSystemManager>(
	isShizukuEnabled,
	ioDispatcher,
	serviceArgs = UserServiceArgs(ComponentName(packageName, RemoteFileSystemService::class.java.name))
		.daemon(false)
		.processNameSuffix("remotefs")
		.tag("RemoteFileSystemService")
		.version(1),
	serviceConnection = RemoteFileSystemConnection()
)

class RemoteFileSystemService : IRemoteFileSystemService.Stub() {
	override fun destroy() = exitProcess(0)
	override fun getFileSystemService() = FileSystemManager.getService()
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