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
import com.github.sisong.HPatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.DefaultDispatcher
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import java.io.FileDescriptor
import java.io.FileOutputStream
import javax.inject.Inject

fun interface BinaryPatcher {
	suspend fun patch(inputPath: Path, outputPath: Path, diffPath: Path): Result<Unit>
}

class BinaryPatcherImpl @Inject constructor(
	@DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem
) : BinaryPatcher {

	@SuppressLint("DiscouragedPrivateApi")
	override suspend fun patch(inputPath: Path, outputPath: Path, diffPath: Path): Result<Unit> {
		try {
			return withContext(ioDispatcher) {
				fileSystem.prepareRecreate(outputPath)
				FileOutputStream(outputPath.toString()).use { outputStream ->
					val fdGetInt = FileDescriptor::class.java.getDeclaredMethod("getInt$")
					val fd = fdGetInt.invoke(outputStream.fd) as Int
					val fdPath = "/proc/self/fd/$fd"
					val result = runInterruptible(defaultDispatcher) {
						HPatch.patch(inputPath.toString(), diffPath.toString(), fdPath)
					}
					if (result == 0) {
						return@withContext Result.success()
					}
					fileSystem.delete(outputPath)
					return@withContext Result.failure(R.string.error_binary_patch_failed, inputPath.toString())
				}
			}
		} catch (throwable: Throwable) {
			fileSystem.delete(outputPath)
			throw throwable
		}
	}
}