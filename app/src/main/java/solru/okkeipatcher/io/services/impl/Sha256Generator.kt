package solru.okkeipatcher.io.services.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import okio.Buffer
import okio.HashingSink.Companion.sha256
import okio.blackholeSink
import okio.buffer
import okio.source
import solru.okkeipatcher.io.services.base.HashGenerator
import solru.okkeipatcher.io.utils.calculateProgressRatio
import solru.okkeipatcher.model.dto.ProgressData
import solru.okkeipatcher.utils.extensions.*
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.ceil

private const val BUFFER_LENGTH: Long = 8192

class Sha256Generator @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
	HashGenerator {

	override suspend fun computeHash(
		inputStream: InputStream,
		size: Long,
		progress: MutableSharedFlow<ProgressData>
	) = withContext(ioDispatcher) {
		progress.reset()
		val progressRatio = calculateProgressRatio(size, BUFFER_LENGTH)
		sha256(blackholeSink()).use { hashingSink ->
			inputStream.source().buffer().use sourceUse@{ source ->
				val progressMax =
					ceil(size.toDouble() / (BUFFER_LENGTH * progressRatio)).toInt()
				var currentProgress = 0
				Buffer().use bufferUse@{ buffer ->
					while (source.read(buffer, BUFFER_LENGTH) > 0) {
						ensureActive()
						hashingSink.write(buffer, buffer.size)
						++currentProgress
						if (currentProgress % progressRatio == 0) {
							progress.emit(currentProgress / progressRatio, progressMax)
						}
					}
					return@use hashingSink.hash.hex()
				}
			}
		}
	}
}