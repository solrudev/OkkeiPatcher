package ru.solrudev.okkeipatcher.domain.model

import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import java.io.Serializable
import java.util.*

/**
 * Represents long-running work.
 */
data class Work(val id: UUID, val label: LocalizedString) : Serializable

/**
 * Represents a [Work] state.
 */
sealed class WorkState {

	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkState()
	data class Failed(val reason: LocalizedString, val stackTrace: String) : WorkState()
	object Succeeded : WorkState()
	object Canceled : WorkState()
	object Unknown : WorkState()

	/**
	 * Returns true for [Failed], [Succeeded] and [Canceled] states.
	 */
	val isFinished: Boolean
		get() = this is Failed || this is Succeeded || this is Canceled
}