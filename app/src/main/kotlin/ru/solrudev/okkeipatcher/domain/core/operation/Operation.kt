package ru.solrudev.okkeipatcher.domain.core.operation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.Message

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
	suspend operator fun invoke(): R
}

/**
 * Converts [ProgressOperation] to [Operation].
 */
fun <R> ProgressOperation<R>.toOperation(): Operation<R> =
	if (this is Operation) this else ProgressOperationWrapper(this)

private class ProgressOperationWrapper<out R>(
	progressOperation: ProgressOperation<R>
) : Operation<R>, ProgressOperation<R> by progressOperation {
	override val status = emptyFlow<LocalizedString>()
	override val messages = emptyFlow<Message>()
}