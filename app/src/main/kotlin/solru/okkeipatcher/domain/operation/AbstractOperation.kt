package solru.okkeipatcher.domain.operation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message

abstract class AbstractOperation<out R> : Operation<R> {
	protected val _status = MutableSharedFlow<LocalizedString>(replay = 1)
	protected val _messages = MutableSharedFlow<Message>(replay = 1)
	protected val _progressDelta = MutableSharedFlow<Int>(replay = 1)
	override val status: Flow<LocalizedString> = _status.asSharedFlow()
	override val messages: Flow<Message> = _messages.asSharedFlow()
	override val progressDelta: Flow<Int> = _progressDelta.asSharedFlow()
}