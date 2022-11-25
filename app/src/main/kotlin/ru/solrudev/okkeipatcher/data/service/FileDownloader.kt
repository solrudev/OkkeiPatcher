package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
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
							onProgressDeltaChanged = { onProgressDeltaChanged(it) }
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