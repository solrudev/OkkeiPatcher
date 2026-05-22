/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import com.github.sisong.HPatch
import com.topjohnwu.superuser.ipc.RootService
import java.io.File
import java.io.FileDescriptor
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class BinaryPatchServiceBinder : IBinaryPatchService.Stub() {

	override fun destroy() = exitProcess(0)

	override fun patchAsync(
		inputPath: String,
		outputPath: String,
		diffPath: String,
		callback: IBinaryPatchCallback
	) {
		thread {
			val outFile = File(outputPath)
			outFile.parentFile?.mkdirs()
			outFile.createNewFile()
			val result = outFile.outputStream().use { outputStream ->
				val fd = outputStream.fd.getInt()
				val fdPath = "/proc/self/fd/$fd"
				HPatch.patch(inputPath, diffPath, fdPath)
			}
			callback.onPatchResult(result)
			exitProcess(result)
		}
	}

	override fun exit(status: Int) {
		thread {
			exitProcess(status)
		}
	}

	class Callback(private val callback: (Int) -> Unit) : IBinaryPatchCallback.Stub() {
		override fun onPatchResult(code: Int) = callback(code)
	}
}

class BinaryPatchService : Service() {
	private val binder = BinaryPatchServiceBinder()
	override fun onBind(intent: Intent?) = binder
}

class RootBinaryPatchService : RootService() {
	private val binder = BinaryPatchServiceBinder()
	override fun onBind(intent: Intent) = binder
}

@SuppressLint("DiscouragedPrivateApi")
private val fdGetInt = FileDescriptor::class.java.getDeclaredMethod("getInt$")

private fun FileDescriptor.getInt() = fdGetInt.invoke(this) as Int