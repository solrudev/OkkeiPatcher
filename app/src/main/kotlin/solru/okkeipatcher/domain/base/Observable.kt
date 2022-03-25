package solru.okkeipatcher.domain.base

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message

interface Observable : ProgressPublisher {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}