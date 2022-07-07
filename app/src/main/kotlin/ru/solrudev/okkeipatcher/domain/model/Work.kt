package ru.solrudev.okkeipatcher.domain.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.*

/**
 * Represents long-running work.
 */
@Keep
data class Work(val id: UUID, val label: LocalizedString) : Serializable

/**
 * Represents a [Work] state.
 */
sealed class WorkState {

	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkState()
	data class Failed(val stackTrace: String) : WorkState()
	object Succeeded : WorkState()
	object Canceled : WorkState()
	object Unknown : WorkState()

	/**
	 * Returns true for [Failed], [Succeeded] and [Canceled] states.
	 */
	val isFinished: Boolean
		get() = this is Failed || this is Succeeded || this is Canceled
}