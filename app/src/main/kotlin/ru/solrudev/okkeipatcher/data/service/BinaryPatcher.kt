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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.util.DEFAULT_PROGRESS_MAX
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

fun interface BinaryPatcher {

	suspend fun patch(
		inputPath: Path,
		outputPath: Path,
		diffPath: Path,
		patchedSize: Long,
		onProgress: suspend (Int) -> Unit
	): Result<Unit>
}

@Reusable
class BinaryPatcherImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem
) : BinaryPatcher {

	private val executor = ioDispatcher.asExecutor()

	override suspend fun patch(
		inputPath: Path,
		outputPath: Path,
		diffPath: Path,
		patchedSize: Long,
		onProgress: suspend (Int) -> Unit
	): Result<Unit> = withContext(ioDispatcher) {
		val progressJob = reportProgressIn(outputPath, patchedSize, onProgress, this)
		try {
			val result = patchCancellable(inputPath, outputPath, diffPath)
			progressJob.cancel()
			if (result == 0) {
				return@withContext Result.success()
			}
			fileSystem.delete(outputPath)
			return@withContext Result.failure(R.string.error_binary_patch_failed, result, inputPath.toString())
		} catch (throwable: Throwable) {
			fileSystem.delete(outputPath)
			throw throwable
		}
	}

	private suspend inline fun patchCancellable(
		inputPath: Path,
		outputPath: Path,
		diffPath: Path
	) = suspendCancellableCoroutine { continuation ->
		val serviceConnection = BinaryPatchServiceConnection(
			continuation,
			applicationContext,
			executor,
			inputPath,
			outputPath,
			diffPath
		)
		applicationContext.bindService(
			Intent(applicationContext, BinaryPatchService::class.java),
			serviceConnection,
			Context.BIND_AUTO_CREATE
		)
	}

	private fun reportProgressIn(
		outputPath: Path,
		patchedSize: Long,
		onProgress: suspend (Int) -> Unit,
		scope: CoroutineScope
	) = scope.launch {
		var previousSize = 0L
		while (isActive) {
			delay(2.seconds)
			val currentSize = fileSystem.metadataOrNull(outputPath)?.size ?: 0
			val delta = currentSize - previousSize
			previousSize = currentSize
			val normalizedDelta = (delta.toDouble() / patchedSize * DEFAULT_PROGRESS_MAX).roundToInt()
			onProgress(normalizedDelta)
		}
	}

	private class BinaryPatchServiceConnection(
		private val continuation: CancellableContinuation<Int>,
		private val context: Context,
		private val executor: Executor,
		private val inputPath: Path,
		private val outputPath: Path,
		private val diffPath: Path
	) : ServiceConnection {

		override fun onServiceConnected(name: ComponentName?, service: IBinder?) = executor.execute {
			if (service == null || !service.pingBinder()) {
				return@execute
			}
			val binaryPatchService = IBinaryPatchService.Stub.asInterface(service)
			val callback = BinaryPatchServiceBinder.Callback(continuation::resume)
			try {
				continuation.invokeOnCancellation {
					try {
						binaryPatchService.exit(-1)
					} catch (_: RemoteException) { // no-op
					}
				}
				binaryPatchService.patchAsync(
					inputPath.toString(),
					outputPath.toString(),
					diffPath.toString(),
					callback
				)
			} catch (exception: RemoteException) {
				continuation.resumeWithException(exception)
			}
		}

		override fun onServiceDisconnected(name: ComponentName?) = executor.execute {
			context.unbindService(this)
		}
	}
}