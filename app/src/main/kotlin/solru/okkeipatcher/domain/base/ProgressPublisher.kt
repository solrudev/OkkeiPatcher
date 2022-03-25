package solru.okkeipatcher.domain.base

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.domain.model.ProgressData

interface ProgressPublisher {
	val progress: Flow<ProgressData>
}