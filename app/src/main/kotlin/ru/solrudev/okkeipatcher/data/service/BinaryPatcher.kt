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
import android.os.IBinder
import android.os.RemoteException
import com.topjohnwu.superuser.ipc.RootService
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.OperationMode
import ru.solrudev.okkeipatcher.app.repository.OperationModeRepository
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.di.DefaultFileSystem
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.di.MainDispatcher
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
	@MainDispatcher mainDispatcher: CoroutineDispatcher,
	@DefaultFileSystem private val fileSystem: FileSystem,
	private val operationModeRepository: OperationModeRepository,
	private val preferencesRepository: PreferencesRepository
) : BinaryPatcher {

	private val mainExecutor = mainDispatcher.asExecutor()

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
	): Int {
		val operationMode = operationModeRepository.getEffectiveOperationModeFlow(
			preferencesRepository.operationMode.flow
		).first()
		val serviceConnection = BinaryPatchServiceConnection
			.create(operationMode, applicationContext, mainExecutor)
			.bindService()
		val service = serviceConnection.awaitService()
			?: error("Unable to connect to BinaryPatchService, operationMode=$operationMode")
		return suspendCancellableCoroutine { continuation ->
			val callback = BinaryPatchServiceBinder.Callback(continuation::resume)
			continuation.invokeOnCancellation {
				try {
					service.exit(-1)
				} catch (_: RemoteException) { // no-op
				}
			}
			try {
				service.patchAsync(inputPath.toString(), outputPath.toString(), diffPath.toString(), callback)
			} catch (e: RemoteException) {
				continuation.resumeWithException(e)
			}
		}
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

	private sealed class BinaryPatchServiceConnection : BaseServiceConnection<IBinaryPatchService>() {

		abstract fun bindService(): BinaryPatchServiceConnection

		override fun getService(service: IBinder): IBinaryPatchService {
			return IBinaryPatchService.Stub.asInterface(service)
		}

		companion object {
			fun create(
				mode: OperationMode,
				context: Context,
				mainExecutor: Executor
			): BinaryPatchServiceConnection {
				return when (mode) {
					OperationMode.NonRoot -> StandardBinaryPatchServiceConnection(context)
					OperationMode.Root -> RootBinaryPatchServiceConnection(context, mainExecutor)
					OperationMode.Shizuku -> {
						val shizukuArgs = UserServiceArgs(
							ComponentName(context.packageName, BinaryPatchServiceBinder::class.java.name)
						)
							.daemon(false)
							.processNameSuffix("binarypatch")
							.tag("BinaryPatchService")
							.version(1)
						ShizukuBinaryPatchServiceConnection(shizukuArgs)
					}
				}
			}
		}
	}

	private class StandardBinaryPatchServiceConnection(
		private val context: Context
	) : BinaryPatchServiceConnection() {

		override fun bindService() = apply {
			context.bindService(
				Intent(context, BinaryPatchService::class.java),
				this,
				Context.BIND_AUTO_CREATE
			)
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			super.onServiceDisconnected(name)
			context.unbindService(this)
		}
	}

	private class ShizukuBinaryPatchServiceConnection(
		private val shizukuArgs: UserServiceArgs
	) : BinaryPatchServiceConnection() {

		override fun bindService() = apply {
			Shizuku.bindUserService(shizukuArgs, this)
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			super.onServiceDisconnected(name)
			Shizuku.unbindUserService(shizukuArgs, this, true)
		}
	}

	private class RootBinaryPatchServiceConnection(
		private val context: Context,
		private val mainExecutor: Executor
	) : BinaryPatchServiceConnection() {

		override fun bindService() = apply {
			mainExecutor.execute {
				RootService.bind(Intent(context, RootBinaryPatchService::class.java), this)
			}
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			super.onServiceDisconnected(name)
			mainExecutor.execute {
				RootService.unbind(this)
			}
		}
	}
}