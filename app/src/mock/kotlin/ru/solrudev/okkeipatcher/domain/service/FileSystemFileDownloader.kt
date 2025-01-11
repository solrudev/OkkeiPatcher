/*
 * Okkei Patcher
 * Copyright (C) 2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.domain.service

import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@Reusable
class FileSystemFileDownloader @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem
) : FileDownloader {

	override val progressMax = STREAM_COPY_PROGRESS_MAX

	override suspend fun download(
		url: String,
		path: Path,
		hashing: Boolean,
		onProgress: suspend (Int) -> Unit
	): String {
		val source = url.toPath()
		return withContext(ioDispatcher) {
			fileSystem.copy(
				source, target = path, hashing,
				onProgressChanged = {
					ensureActive()
					delay(50.milliseconds) // simulate slow internet connection
					onProgress(it)
				}
			)
		}
	}
}