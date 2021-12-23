package solru.okkeipatcher.core.services

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.core.base.ProgressProvider
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.Message

interface ObservableService : ProgressProvider {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}