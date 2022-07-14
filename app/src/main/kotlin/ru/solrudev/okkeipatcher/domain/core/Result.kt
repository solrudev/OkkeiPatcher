package ru.solrudev.okkeipatcher.domain.core

/**
 * Represents either a successful result or a failure with a reason.
 */
sealed interface Result {
	object Success : Result
	data class Failure(val reason: LocalizedString) : Result
}

/**
 * Executes [block] if the result is [Result.Failure] and returns itself.
 */
inline fun <R> Result.onFailure(block: (Result.Failure) -> R): Result {
	if (this is Result.Failure) block(this)
	return this
}