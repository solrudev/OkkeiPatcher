package solru.okkeipatcher.core.progress

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.model.dto.ProgressData

interface ProgressPublisher {
	val progress: Flow<ProgressData>
}