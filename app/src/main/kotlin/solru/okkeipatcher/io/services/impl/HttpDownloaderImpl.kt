package solru.okkeipatcher.io.services.impl

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp312.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okio.buffer
import okio.sink
import solru.okkeipatcher.io.exceptions.HttpStatusCodeException
import solru.okkeipatcher.io.services.base.HttpDownloader
import solru.okkeipatcher.io.utils.calculateProgressRatio
import solru.okkeipatcher.model.dto.ProgressData
import java.io.OutputStream
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil

private const val BUFFER_LENGTH: Long = 8192

class HttpDownloaderImpl @Inject constructor(
	private val ioDispatcher: CoroutineDispatcher,
	private val okHttpClient: OkHttpClient
) : HttpDownloader {

	private val client: HttpClient by lazy {
		HttpClient(OkHttp312) {
			engine {
				preconfigured = okHttpClient
			}
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun download(
		url: String,
		outputStream: OutputStream,
		onProgressChanged: suspend (ProgressData) -> Unit
	) = withContext(ioDispatcher) {
		onProgressChanged(ProgressData())
		client.get<HttpStatement>(url).execute { httpResponse ->
			if (httpResponse.status.value != 200) {
				throw HttpStatusCodeException(httpResponse.status)
			}
			val channel: ByteReadChannel = httpResponse.receive()
			val contentLength = httpResponse.contentLength() ?: Int.MAX_VALUE.toLong()
			val progressRatio = calculateProgressRatio(contentLength, BUFFER_LENGTH)
			val progressMax =
				ceil(contentLength.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
			var currentProgress = 0
			var transferredBytes: Long = 0
			outputStream.sink().buffer().use { sink ->
				while (!channel.isClosedForRead) {
					ensureActive()
					val packet = channel.readRemaining(BUFFER_LENGTH)
					while (packet.isNotEmpty) {
						ensureActive()
						val bytes = packet.readBytes()
						sink.write(bytes)
						transferredBytes += bytes.size
						if (transferredBytes % BUFFER_LENGTH == 0L || packet.isEmpty) {
							++currentProgress
						}
						if (currentProgress % progressRatio == 0) {
							onProgressChanged(ProgressData(currentProgress / progressRatio, progressMax))
						}
					}
				}
			}
		}
	}
}