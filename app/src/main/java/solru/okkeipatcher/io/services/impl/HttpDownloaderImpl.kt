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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import solru.okkeipatcher.exceptions.io.HttpStatusCodeException
import solru.okkeipatcher.io.services.base.HttpDownloader
import solru.okkeipatcher.io.utils.calculateProgressRatio
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.utils.extensions.emit
import solru.okkeipatcher.utils.extensions.reset
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.io.use
import kotlin.math.ceil

private const val BUFFER_LENGTH: Long = 8192

class HttpDownloaderImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
	HttpDownloader {

	private val client: HttpClient by lazy {
		HttpClient(OkHttp312) {
			engine {
				config {
					followRedirects(true)
					followSslRedirects(true)
					readTimeout(0, TimeUnit.SECONDS)
					writeTimeout(0, TimeUnit.SECONDS)
				}
			}
		}
	}

	override suspend fun download(
		url: String,
		outputStream: OutputStream,
		progress: MutableSharedFlow<ProgressData>
	) = withContext(ioDispatcher) {
		progress.reset()
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
						if ((transferredBytes % BUFFER_LENGTH).toInt() == 0 || packet.isEmpty) {
							++currentProgress
						}
						if (currentProgress % progressRatio == 0) {
							progress.emit(currentProgress / progressRatio, progressMax)
						}
					}
				}
			}
		}
	}
}