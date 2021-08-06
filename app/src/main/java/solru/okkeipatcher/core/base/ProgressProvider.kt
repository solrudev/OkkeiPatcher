package solru.okkeipatcher.core.base

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.model.dto.ProgressData

interface ProgressProvider {
	val progress: Flow<ProgressData>
}