package solru.okkeipatcher.core.services

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.core.progress.ProgressPublisher
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message

interface ObservableService : ProgressPublisher {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}