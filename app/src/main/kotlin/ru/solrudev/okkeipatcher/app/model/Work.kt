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

package ru.solrudev.okkeipatcher.app.model

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
sealed interface WorkState {

	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkState
	data class Failed(val reason: LocalizedString, val stackTrace: String) : WorkState
	data object Succeeded : WorkState
	data object Canceled : WorkState
	data object Unknown : WorkState

	/**
	 * Returns true for [Failed], [Succeeded] and [Canceled] states.
	 */
	val isFinished: Boolean
		get() = this is Failed || this is Succeeded || this is Canceled
}