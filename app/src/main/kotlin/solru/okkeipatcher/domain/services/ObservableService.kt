package solru.okkeipatcher.domain.services

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.domain.progress.ProgressPublisher

interface ObservableService : ProgressPublisher {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}