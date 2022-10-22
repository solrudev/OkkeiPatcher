package ru.solrudev.okkeipatcher.domain.core.operation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.core.onFailure

/**
 * Creates an instance of [Operation] which executes the provided [block] when invoked. This builder function allows
 * to emit progress delta, status and messages via [OperationScope].
 *
 * When operation created with this builder function is finished successfully, it will emit remaining progress delta
 * if accumulated progress hasn't reached max progress.
 *
 * Throws [IllegalArgumentException] if values of parameters are unsupported.
 *
 * @param operations nested operations that are part of the new operation (defaults to an empty array). Their progress,
 * status and message flows will be merged with the newly created operation's flows, and their max progress values will
 * be summed.
 * @param progressMax max progress of the operation (cannot be negative, defaults to zero). Provided value should
 * account only for actions executed in [block], without nested operations' max progress values.
 * @param canInvoke lambda which returns if the operation can be invoked at the moment. By default returns
 * [Result.Success].
 */
fun <R> operation(
	vararg operations: Operation<*> = emptyArray(),
	progressMax: Int = 0,
	canInvoke: suspend () -> Result = { Result.Success },
	block: suspend OperationScope.() -> R
): Operation<R> {
	require(progressMax >= 0) { "progressMax cannot be negative, but was $progressMax" }
	return OperationImpl(operations, progressMax, canInvoke, block)
}

/**
 * Returns an operation which aggregates [operations] and executes them one by one sequentially.
 *
 * When operation created with this builder function is finished successfully, it will emit remaining progress delta
 * if accumulated progress hasn't reached max progress.
 */
fun aggregateOperation(vararg operations: Operation<*>): Operation<Unit> = OperationImpl(
	operations,
	canInvokeDelegate = lambda@{
		operations.forEach { operation ->
			operation.canInvoke().onFailure { return@lambda it }
		}
		Result.Success
	},
	block = {
		operations.forEach { it.invoke() }
	}
)

/**
 * Returns an operation which does nothing.
 */
fun emptyOperation(): Operation<Unit> = EmptyOperation

private object EmptyOperation : Operation<Unit> {
	override val status = emptyFlow<LocalizedString>()
	override val messages = emptyFlow<Message>()
	override val progressDelta = emptyFlow<Int>()
	override val progressMax = 0
	override suspend fun invoke() {}
}

private class OperationImpl<out R>(
	operations: Array<out Operation<*>>,
	progressMax: Int = 0,
	private val canInvokeDelegate: suspend () -> Result = { Result.Success },
	private val block: suspend OperationScope.() -> R
) : Operation<R>, OperationScope {

	private val _status = MutableSharedFlow<LocalizedString>(replay = 1)
	private val _messages = MutableSharedFlow<Message>(replay = 1)
	private val _progressDelta = MutableSharedFlow<Int>(replay = 1)

	override val status = operations
		.map { it.status }
		.plus(_status)
		.merge()

	override val messages = operations
		.map { it.messages }
		.plus(_messages)
		.merge()

	override val progressDelta = operations
		.map { it.progressDelta }
		.plus(_progressDelta)
		.merge()

	override val progressMax = progressMax + operations.sumOf { it.progressMax }
	private var accumulatedProgress = 0

	private val remainingProgress: Int
		get() = progressMax - accumulatedProgress

	override suspend fun canInvoke() = canInvokeDelegate()

	override suspend fun invoke() = coroutineScope {
		val accumulateProgressJob = if (progressMax > 0) accumulateProgress() else null
		try {
			val result = block()
			progressDelta(remainingProgress)
			result
		} finally {
			accumulateProgressJob?.cancel()
			accumulatedProgress = 0
		}
	}

	override suspend fun status(status: LocalizedString) = _status.emit(status)
	override suspend fun message(message: Message) = _messages.emit(message)

	override suspend fun progressDelta(progressDelta: Int) {
		val coercedProgressDelta = progressDelta.coerceIn(-accumulatedProgress, remainingProgress)
		if (coercedProgressDelta != 0) {
			_progressDelta.emit(coercedProgressDelta)
		}
	}

	private fun CoroutineScope.accumulateProgress() = progressDelta
		.runningReduce(Int::plus)
		.conflate()
		.onEach { accumulatedProgress = it }
		.launchIn(this)
}