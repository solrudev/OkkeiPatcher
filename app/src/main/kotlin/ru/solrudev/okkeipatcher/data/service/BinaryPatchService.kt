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
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import com.github.sisong.HPatch
import java.io.File
import java.io.FileDescriptor
import kotlin.concurrent.thread
import kotlin.system.exitProcess

private const val TAG = "BinaryPatchService"

class BinaryPatchService : Service() {

	private val clients = mutableListOf<Messenger>()
	private val messenger = Messenger(BinaryPatchHandler())

	override fun onBind(intent: Intent?): IBinder {
		return messenger.binder
	}

	@SuppressLint("HandlerLeak")
	private inner class BinaryPatchHandler : Handler(Looper.getMainLooper()) {
		override fun handleMessage(msg: Message) = when (msg.what) {
			MSG_START -> {
				clients += msg.replyTo
				startPatch(msg.data)
			}

			MSG_CANCEL -> exitProcess(EXIT_STATUS_CANCELLED)
			else -> {}
		}
	}

	private fun startPatch(data: Bundle) {
		val inputPath = requireNotNull(data.getString(DATA_INPUT_PATH)) { "$TAG: no input path provided" }
		val outputPath = requireNotNull(data.getString(DATA_OUTPUT_PATH)) { "$TAG: no output path provided" }
		val diffPath = requireNotNull(data.getString(DATA_DIFF_PATH)) { "$TAG: no diff path provided" }
		thread {
			val outFile = File(outputPath)
			outFile.parentFile?.mkdirs()
			outFile.createNewFile()
			val result = outFile.outputStream().use { outputStream ->
				val fd = outputStream.fd.getInt()
				val fdPath = "/proc/self/fd/$fd"
				HPatch.patch(inputPath, diffPath, fdPath)
			}
			sendResult(result)
			exitProcess(result)
		}
	}

	private fun sendResult(result: Int) {
		for (client in clients.asReversed()) {
			try {
				client.send(Message.obtain(null, MSG_RESULT, result, 0))
			} catch (_: RemoteException) {
				clients -= client
			}
		}
	}

	companion object {

		const val MSG_START = 1
		const val MSG_CANCEL = 2
		const val MSG_RESULT = 3
		const val DATA_INPUT_PATH = "INPUT_PATH"
		const val DATA_OUTPUT_PATH = "OUTPUT_PATH"
		const val DATA_DIFF_PATH = "DIFF_PATH"
		const val EXIT_STATUS_CANCELLED = -1

		@SuppressLint("DiscouragedPrivateApi")
		private val fdGetInt = FileDescriptor::class.java.getDeclaredMethod("getInt$")

		private fun FileDescriptor.getInt() = fdGetInt.invoke(this) as Int
	}
}