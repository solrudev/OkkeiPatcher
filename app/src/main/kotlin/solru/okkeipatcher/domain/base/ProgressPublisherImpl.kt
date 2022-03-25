package solru.okkeipatcher.domain.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.domain.model.ProgressData

class ProgressPublisherImpl : ProgressPublisher {

	val _progress = MutableSharedFlow<ProgressData>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val progress: Flow<ProgressData> = _progress.asSharedFlow()
}