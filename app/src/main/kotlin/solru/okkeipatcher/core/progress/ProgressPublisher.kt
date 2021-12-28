package solru.okkeipatcher.core.progress

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.data.ProgressData

interface ProgressPublisher {
	val progress: Flow<ProgressData>
}