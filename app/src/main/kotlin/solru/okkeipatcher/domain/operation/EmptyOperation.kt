package solru.okkeipatcher.domain.operation

import kotlinx.coroutines.flow.emptyFlow
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message

/**
 * Operation which does nothing.
 */
object EmptyOperation : Operation<Unit> {
	override val status = emptyFlow<LocalizedString>()
	override val messages = emptyFlow<Message>()
	override val progressDelta = emptyFlow<Int>()
	override val progressMax = 0
	override suspend fun invoke() {}
}