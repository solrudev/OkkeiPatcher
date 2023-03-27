package ru.solrudev.okkeipatcher.domain.core

import androidx.annotation.StringRes

/**
 * Represents either a successful result or a failure with a reason.
 */
sealed interface Result {

	object Success : Result
	data class Failure(val reason: LocalizedString) : Result

	companion object {

		/**
		 * Returns [Success].
		 */
		fun success() = Success

		/**
		 * Creates [Failure] instance with a [LocalizedString] represented by Android resource string as its
		 * [Failure.reason].
		 */
		fun failure(@StringRes resourceId: Int, vararg args: Any): Failure {
			return Failure(LocalizedString.resource(resourceId, args))
		}

		/**
		 * Creates [Failure] instance with a [LocalizedString] represented by hardcoded [value] as its [Failure.reason].
		 */
		fun failure(value: CharSequence): Failure {
			return Failure(LocalizedString.raw(value))
		}

		/**
		 * Creates [Failure] instance with a [reason].
		 */
		fun failure(reason: LocalizedString): Failure {
			return Failure(reason)
		}
	}
}

/**
 * Executes [block] if the result is [Result.Failure] and returns result unmodified.
 */
inline fun <R> Result.onFailure(block: (Result.Failure) -> R): Result {
	if (this is Result.Failure) block(this)
	return this
}

/**
 * Executes [block] if the result is [Result.Success] and returns result unmodified.
 */
inline fun <R> Result.onSuccess(block: (Result.Success) -> R): Result {
	if (this is Result.Success) block(this)
	return this
}