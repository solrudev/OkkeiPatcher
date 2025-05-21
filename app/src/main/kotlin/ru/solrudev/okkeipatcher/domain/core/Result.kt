/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.domain.core

import androidx.annotation.StringRes
import java.io.Serializable

/**
 * Represents either a successful result or a failure with a reason.
 */
sealed interface Result<T> {

	data class Success<T>(val value: T) : Result<T>
	data class Failure<T>(val reason: LocalizedString) : Result<T>

	companion object {

		private val successUnit = Success(Unit)

		/**
		 * Returns [Success] with [Unit] as a value.
		 */
		fun success() = successUnit

		/**
		 * Creates [Success] instance with a [value].
		 */
		fun <T> success(value: T) = Success(value)

		/**
		 * Creates [Failure] instance with a [LocalizedString] represented by Android resource string as its
		 * [Failure.reason].
		 */
		fun <T> failure(@StringRes resourceId: Int, vararg args: Serializable): Failure<T> {
			return Failure(LocalizedString.resource(resourceId, *args))
		}

		/**
		 * Creates [Failure] instance with a [LocalizedString] represented by hardcoded [value] as its [Failure.reason].
		 */
		fun <T> failure(value: CharSequence): Failure<T> {
			return Failure(LocalizedString.raw(value))
		}

		/**
		 * Creates [Failure] instance with a [reason].
		 */
		fun <T> failure(reason: LocalizedString): Failure<T> {
			return Failure(reason)
		}
	}
}

/**
 * Executes [block] if the result is [Result.Failure] and returns result unmodified.
 */
inline fun <T> Result<T>.onFailure(block: (Result.Failure<T>) -> Unit): Result<T> {
	if (this is Result.Failure) block(this)
	return this
}

/**
 * Executes [block] if the result is [Result.Success] and returns result unmodified.
 */
inline fun <T> Result<T>.onSuccess(block: (Result.Success<T>) -> Unit): Result<T> {
	if (this is Result.Success<T>) block(this)
	return this
}