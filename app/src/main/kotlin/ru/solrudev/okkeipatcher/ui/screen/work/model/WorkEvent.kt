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

package ru.solrudev.okkeipatcher.ui.screen.work.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.shared.model.HasWork
import ru.solrudev.okkeipatcher.ui.shared.model.WorkStateEventFactory

sealed interface WorkEvent : JetEvent {
	data class CancelWork(override val work: Work) : WorkEvent, WorkEffect, HasWork
	data class StartObservingWork(override val work: Work) : WorkEvent, WorkEffect, HasWork
	data object CancelRequested : WorkEvent
	data object CancelMessageShown : WorkEvent
	data object CancelMessageDismissed : WorkEvent
	data object ErrorShown : WorkEvent
	data object ErrorDismissed : WorkEvent
	data object AnimationsPlayed : WorkEvent
	data object ViewHidden : WorkEvent
}

sealed interface WorkStateEvent : WorkEvent {
	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkStateEvent
	data class Failed(val reason: LocalizedString, val stackTrace: String) : WorkStateEvent
	data object Succeeded : WorkStateEvent
	data object Canceled : WorkStateEvent
	data object Unknown : WorkStateEvent
}

object WorkStateEventFactoryForWorkScreen : WorkStateEventFactory<WorkEvent> {
	override fun running(status: LocalizedString, progressData: ProgressData) = WorkStateEvent.Running(status, progressData)
	override fun failed(reason: LocalizedString, stackTrace: String) = WorkStateEvent.Failed(reason, stackTrace)
	override fun succeeded() = WorkStateEvent.Succeeded
	override fun canceled() = WorkStateEvent.Canceled
	override fun unknown() = WorkStateEvent.Unknown
}

sealed interface WorkEffect : JetEffect