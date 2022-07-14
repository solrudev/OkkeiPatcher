package ru.solrudev.okkeipatcher.domain.core.operation

import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message

/**
 * Scope for the [operation] builder.
 */
interface OperationScope {

	/**
	 * Emits operation's status.
	 */
	suspend fun status(status: LocalizedString)

	/**
	 * Emits operation's message.
	 */
	suspend fun message(message: Message)

	/**
	 * Emits operation's progress delta. Zeroes are skipped.
	 *
	 * If [progressDelta] is greater than remaining progress, it will be coerced to the remaining progress (zero if
	 * emitted progress has reached max progress).
	 *
	 * If [progressDelta] is less than negative of already emitted progress, it will be coerced to the negative of
	 * emitted progress (zero if there was no emitted progress yet).
	 */
	suspend fun progressDelta(progressDelta: Int)
}