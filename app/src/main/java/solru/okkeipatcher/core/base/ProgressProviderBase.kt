package solru.okkeipatcher.core.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.model.dto.ProgressData

open class ProgressProviderBase : ProgressProvider {

	protected val progressMutable = MutableSharedFlow<ProgressData>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val progress: Flow<ProgressData> = progressMutable.asSharedFlow()
}