package ru.solrudev.okkeipatcher.domain.core.operation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.Result

/**
 * Operation which reports its progress, status and messages. [R] is the type of operation result.
 */
interface Operation<out R> : ProgressOperation<R> {
	val status: Flow<LocalizedString>
	val messages: Flow<Message>
}

/**
 * Operation which reports its progress. [R] is the type of operation result.
 */
interface ProgressOperation<out R> {
	val progressDelta: Flow<Int>
	val progressMax: Int
	suspend fun canInvoke(): Result = Result.Success
	suspend operator fun invoke(): R
}

/**
 * Converts [ProgressOperation] to [Operation].
 */
fun <R> ProgressOperation<R>.asOperation(): Operation<R> {
	return if (this is Operation) this else ProgressOperationWrapper(this)
}

@JvmInline
private value class ProgressOperationWrapper<out R>(
	private val progressOperation: ProgressOperation<R>
) : Operation<R>, ProgressOperation<R> by progressOperation {

	override val status: Flow<LocalizedString>
		get() = emptyFlow()

	override val messages: Flow<Message>
		get() = emptyFlow()
}