package solru.okkeipatcher.domain.operation

import kotlinx.coroutines.flow.*
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

/**
 * Aggregates other operations and executes them one by one sequentially.
 */
open class AggregateOperation(private val operations: List<Operation<*>>) : Operation<Unit> {

	final override val status = operations
		.map { it.status }
		.merge()

	final override val messages = operations
		.map { it.messages }
		.merge()

	final override val progressDelta = operations
		.map { it.progressDelta }
		.merge()

	final override val progressMax = operations.sumOf { it.progressMax }

	final override suspend fun invoke() {
		preInvoke()
		operations.forEach { it.invoke() }
		postInvoke()
	}

	protected open suspend fun preInvoke() {}
	protected open suspend fun postInvoke() {}
}

/**
 * Base abstract class for operations which has mutable shared flows to emit to.
 */
abstract class AbstractOperation<out R> : Operation<R> {
	protected val _status = MutableSharedFlow<LocalizedString>(replay = 1)
	protected val _messages = MutableSharedFlow<Message>(replay = 1)
	protected val _progressDelta = MutableSharedFlow<Int>(replay = 1)
	override val status: Flow<LocalizedString> = _status.asSharedFlow()
	override val messages: Flow<Message> = _messages.asSharedFlow()
	override val progressDelta: Flow<Int> = _progressDelta.asSharedFlow()
}