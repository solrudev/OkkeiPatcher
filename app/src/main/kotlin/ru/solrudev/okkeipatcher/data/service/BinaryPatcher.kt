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

import com.github.sisong.HPatch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.DefaultDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import javax.inject.Inject

fun interface BinaryPatcher {
	suspend fun patch(inputPath: Path, outputPath: Path, diffPath: Path): Result<Unit>
}

class BinaryPatcherImpl @Inject constructor(
	@DefaultDispatcher
	private val defaultDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem
) : BinaryPatcher {

	override suspend fun patch(inputPath: Path, outputPath: Path, diffPath: Path): Result<Unit> {
		try {
			val result = runInterruptible(defaultDispatcher) {
				HPatch.patch(inputPath.toString(), diffPath.toString(), outputPath.toString())
			}
			if (result == 0) {
				return Result.success()
			}
			fileSystem.delete(outputPath)
			return Result.failure(R.string.error_binary_patch_failed, inputPath.toString())
		} catch (throwable: Throwable) {
			fileSystem.delete(outputPath)
			throw throwable
		}
	}
}