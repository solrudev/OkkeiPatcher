package solru.okkeipatcher.core.base

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.model.dto.Message

interface AppService : ProgressProvider {
	val status: Flow<Int>
	val message: Flow<Message>
}