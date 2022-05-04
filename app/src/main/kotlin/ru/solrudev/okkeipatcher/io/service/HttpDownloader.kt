package ru.solrudev.okkeipatcher.io.service

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
import okio.blackholeSink
import okio.buffer
import okio.sink
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.io.exception.HttpStatusCodeException
import ru.solrudev.okkeipatcher.io.util.BlackholeOutputStream
import ru.solrudev.okkeipatcher.io.util.calculateProgressRatio
import java.io.OutputStream
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil
import kotlin.math.roundToInt

private const val BUFFER_LENGTH = 8192L

interface HttpDownloader {

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return Output hash. Empty string if [hashing] is `false`.
	 */
	suspend fun download(
		url: String,
		outputStream: OutputStream,
		hashing: Boolean = false,
		onProgressDeltaChanged: suspend (Int) -> Unit
	): String
}

class HttpDownloaderImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	okHttpClient: OkHttpClient
) : HttpDownloader {

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
		outputStream: OutputStream,
		hashing: Boolean,
		onProgressDeltaChanged: suspend (Int) -> Unit
	) = withContext(ioDispatcher) {
		client.prepareGet(url).execute { httpResponse ->
			if (httpResponse.status.value != 200) {
				throw HttpStatusCodeException(httpResponse.status)
			}
			val channel = httpResponse.bodyAsChannel()
			val contentLength = httpResponse.contentLength() ?: Int.MAX_VALUE.toLong()
			val progressRatio = calculateProgressRatio(contentLength, BUFFER_LENGTH)
			val progressMax = ceil(contentLength.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
			val progressDelta = (100.0 / progressMax).roundToInt()
			var currentProgress = 0
			var transferredBytes = 0L
			val baseSink = if (outputStream is BlackholeOutputStream) blackholeSink() else outputStream.sink()
			val sink = if (hashing) HashingSink.sha256(baseSink) else baseSink
			sink.buffer().use { bufferedSink ->
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
				if (sink is HashingSink) sink.hash.hex() else ""
			}
		}
	}
}