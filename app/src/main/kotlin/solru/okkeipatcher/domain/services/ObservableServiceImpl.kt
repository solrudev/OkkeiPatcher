package solru.okkeipatcher.domain.services

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.Message
import solru.okkeipatcher.domain.progress.ProgressPublisher
import solru.okkeipatcher.domain.progress.ProgressPublisherImpl

open class ObservableServiceImpl(
	protected val progressPublisher: ProgressPublisherImpl = ProgressPublisherImpl()
) : ObservableService, ProgressPublisher by progressPublisher {

	protected val mutableStatus = MutableSharedFlow<LocalizedString>()

	protected val mutableMessages = MutableSharedFlow<Message>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val status: Flow<LocalizedString> = mutableStatus.asSharedFlow()
	override val messages: Flow<Message> = mutableMessages.asSharedFlow()
}