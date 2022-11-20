package ru.solrudev.okkeipatcher.data.service

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Sink
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.service.util.await
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.copyTo
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
		sink: Sink,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit = {}
	): String
}

class FileDownloaderImpl @Inject constructor(private val okHttpClient: OkHttpClient) : FileDownloader {

	override val progressMax = STREAM_COPY_PROGRESS_MAX

	override suspend fun download(
		url: String,
		sink: Sink,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	): String {
		sink.use { downloadSink ->
			try {
				val request = Request.Builder().url(url).build()
				val responseBody = okHttpClient.newCall(request).await().body() ?: return ""
				return responseBody.source().copyTo(
					downloadSink,
					responseBody.contentLength(),
					hashing,
					onProgressDeltaChanged = { onProgressDeltaChanged(it) }
				)
			} catch (_: NetworkNotAvailableException) {
				throw NoNetworkException()
			}
		}
	}
}