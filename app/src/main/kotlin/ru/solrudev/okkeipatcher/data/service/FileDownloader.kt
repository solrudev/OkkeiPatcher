package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.Sink
import okio.sink
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.service.util.await
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import java.io.File
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

/**
 * @param hashing Does output stream need to be hashed. Default is `false`.
 * @return File hash. Empty string if [hashing] is `false`.
 */
suspend inline fun FileDownloader.download(
	url: String,
	outputFile: File,
	ioDispatcher: CoroutineDispatcher,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit
): String {
	val sink = withContext(ioDispatcher) {
		outputFile.recreate()
		outputFile.sink()
	}
	return download(url, sink, hashing, onProgressDeltaChanged)
}

class FileDownloaderImpl @Inject constructor(
	private val okHttpClient: OkHttpClient,
	private val streamCopier: StreamCopier
) : FileDownloader {

	override val progressMax = 100

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
				return streamCopier.copy(
					responseBody.source(),
					downloadSink,
					responseBody.contentLength(),
					hashing,
					onProgressDeltaChanged
				)
			} catch (_: NetworkNotAvailableException) {
				throw NoNetworkException()
			}
		}
	}
}