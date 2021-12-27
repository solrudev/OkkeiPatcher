package solru.okkeipatcher.core.services

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.core.progress.ProgressPublisher
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.Message

interface ObservableService : ProgressPublisher {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}