package solru.okkeipatcher.domain.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message

abstract class ObservableImpl(
	protected val progressPublisher: ProgressPublisherImpl = ProgressPublisherImpl()
) : Observable, ProgressPublisher by progressPublisher {

	protected val _status = MutableSharedFlow<LocalizedString>()

	protected val _messages = MutableSharedFlow<Message>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val status: Flow<LocalizedString> = _status.asSharedFlow()
	override val messages: Flow<Message> = _messages.asSharedFlow()
}