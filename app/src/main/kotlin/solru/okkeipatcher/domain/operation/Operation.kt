package solru.okkeipatcher.domain.operation

import kotlinx.coroutines.flow.Flow
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.model.Message

/**
 * Operation which reports its progress, status and, possibly, messages. Generic type parameter is used
 * as a return type for [invoke] operator.
 */
interface Operation<out R> {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
	val progressDelta: Flow<Int>
	val progressMax: Int
	suspend operator fun invoke(): R
}