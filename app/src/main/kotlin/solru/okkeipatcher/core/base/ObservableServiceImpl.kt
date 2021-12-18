package solru.okkeipatcher.core.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.Message

open class ObservableServiceImpl(
	protected val progressProvider: ProgressProviderImpl = ProgressProviderImpl()
) : ObservableService, ProgressProvider by progressProvider {

	protected val mutableStatus = MutableSharedFlow<LocalizedString>()

	protected val mutableMessages = MutableSharedFlow<Message>(
		extraBufferCapacity = 1,
		onBufferOverflow = BufferOverflow.DROP_OLDEST
	)

	override val status: Flow<LocalizedString> = mutableStatus.asSharedFlow()
	override val messages: Flow<Message> = mutableMessages.asSharedFlow()
}