package ru.solrudev.okkeipatcher.data.service

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okio.HashingSink
import okio.Sink
import okio.buffer
import okio.sink
import ru.solrudev.okkeipatcher.data.network.model.exception.NetworkNotAvailableException
import ru.solrudev.okkeipatcher.data.service.util.calculateProgressRatio
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.model.exception.NoNetworkException
import java.io.File
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val BUFFER_LENGTH = 8192L

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
@Suppress("BlockingMethodInNonBlockingContext")
suspend inline fun FileDownloader.download(
	url: String,
	outputFile: File,
	ioDispatcher: CoroutineDispatcher,
	hashing: Boolean = false,
	noinline onProgressDeltaChanged: suspend (Int) -> Unit
): String {
	outputFile.recreate()
	val sink = withContext(ioDispatcher) { outputFile.sink() }
	return download(url, sink, hashing, onProgressDeltaChanged)
}

class FileDownloaderImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	okHttpClient: OkHttpClient
) : FileDownloader {

	override val progressMax = 100

	private val client by lazy {
		HttpClient(OkHttp) {
			engine {
				preconfigured = okHttpClient
			}
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun download(
		url: String,
		sink: Sink,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	) = try {
		withContext(ioDispatcher) {
			client.prepareGet(url).execute { httpResponse ->
				val channel = httpResponse.bodyAsChannel()
				val contentLength = httpResponse.contentLength() ?: Int.MAX_VALUE.toLong()
				val progressRatio = calculateProgressRatio(contentLength, BUFFER_LENGTH)
				val progressMax = ceil(contentLength.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
				val progressDelta = (100.0 / progressMax).roundToInt()
				var currentProgress = 0
				var transferredBytes = 0L
				val finalSink = if (hashing) HashingSink.sha256(sink) else sink
				finalSink.buffer().use { bufferedSink ->
					while (!channel.isClosedForRead) {
						ensureActive()
						val packet = channel.readRemaining(BUFFER_LENGTH)
						while (packet.isNotEmpty) {
							ensureActive()
							val bytes = packet.readBytes()
							bufferedSink.write(bytes)
							transferredBytes += bytes.size
							if (transferredBytes % BUFFER_LENGTH == 0L || packet.isEmpty) {
								currentProgress++
							}
							if (currentProgress % progressRatio == 0) {
								onProgressDeltaChanged(progressDelta)
							}
						}
					}
					onProgressDeltaChanged(100 - currentProgress / progressRatio)
					bufferedSink.flush()
					if (finalSink is HashingSink) finalSink.hash.hex() else ""
				}
			}
		}
	} catch (_: NetworkNotAvailableException) {
		throw NoNetworkException()
	}
}