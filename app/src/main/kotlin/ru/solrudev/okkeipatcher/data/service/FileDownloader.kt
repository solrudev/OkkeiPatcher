/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
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

import dagger.Reusable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.FileSystem
import okio.HashingSink
import okio.HashingSink.Companion.sha256
import okio.Path
import okio.buffer
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.service.util.await
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.copyTo
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import javax.inject.Inject

interface FileDownloader {

	val progressMax: Int

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun download(
		url: String,
		path: Path,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}

@Reusable
class FileDownloaderImpl @Inject constructor(
	private val okHttpClient: OkHttpClient,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val fileSystem: FileSystem
) : FileDownloader {

	override val progressMax = STREAM_COPY_PROGRESS_MAX

	override suspend fun download(
		url: String,
		path: Path,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	): String {
		try {
			val request = Request.Builder().url(url).build()
			val responseBody = okHttpClient.newCall(request).await().body() ?: return ""
			return withContext(ioDispatcher) {
				responseBody.source().use { source ->
					fileSystem.prepareRecreate(path)
					val sink = if (hashing) sha256(fileSystem.sink(path)) else fileSystem.sink(path)
					sink.buffer().use { bufferedSink ->
						source.copyTo(
							bufferedSink, responseBody.contentLength(),
							onProgressDeltaChanged = {
								ensureActive()
								onProgressDeltaChanged(it)
							}
						)
					}
					return@withContext if (sink is HashingSink) sink.hash.hex() else ""
				}
			}
		} catch (_: NetworkNotAvailableException) {
			throw NoNetworkException()
		}
	}
}