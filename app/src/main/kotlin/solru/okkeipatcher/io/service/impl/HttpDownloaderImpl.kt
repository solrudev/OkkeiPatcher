package solru.okkeipatcher.io.service.impl

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okio.HashingSink
import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.buffer
import okio.sink
import solru.okkeipatcher.di.module.IoDispatcher
import solru.okkeipatcher.domain.model.ProgressData
import solru.okkeipatcher.io.exception.HttpStatusCodeException
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.io.util.BlackholeOutputStream
import solru.okkeipatcher.io.util.calculateProgressRatio
import solru.okkeipatcher.util.extension.empty
import java.io.OutputStream
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil

private const val BUFFER_LENGTH = 8192L

class HttpDownloaderImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	okHttpClient: OkHttpClient
) : HttpDownloader {

	private val client: HttpClient by lazy {
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
		onProgressChanged: suspend (ProgressData) -> Unit
	) = withContext(ioDispatcher) {
		onProgressChanged(ProgressData())
		client.get<HttpStatement>(url).execute { httpResponse ->
			if (httpResponse.status.value != 200) {
				throw HttpStatusCodeException(httpResponse.status)
			}
			val channel = httpResponse.receive<ByteReadChannel>()
			val contentLength = httpResponse.contentLength() ?: Int.MAX_VALUE.toLong()
			val progressRatio = calculateProgressRatio(contentLength, BUFFER_LENGTH)
			val progressMax = ceil(contentLength.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
			var currentProgress = 0
			var transferredBytes = 0L
			val baseSink = if (outputStream is BlackholeOutputStream) blackholeSink() else outputStream.sink()
			val sink = if (hashing) sha256(baseSink) else baseSink
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
							onProgressChanged(ProgressData(currentProgress / progressRatio, progressMax))
						}
					}
				}
				bufferedSink.flush()
				onProgressChanged(ProgressData(progressMax, progressMax))
				if (sink is HashingSink) sink.hash.hex() else String.empty
			}
		}
	}
}