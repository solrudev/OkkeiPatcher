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
	object CancelRequested : WorkEvent
	object CancelMessageShown : WorkEvent
	object CancelMessageDismissed : WorkEvent
	object ErrorShown : WorkEvent
	object ErrorDismissed : WorkEvent
	object AnimationsPlayed : WorkEvent
	object ViewHidden : WorkEvent
}

sealed interface WorkStateEvent : WorkEvent {
	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkStateEvent
	data class Failed(val reason: LocalizedString, val stackTrace: String) : WorkStateEvent
	object Succeeded : WorkStateEvent
	object Canceled : WorkStateEvent
	object Unknown : WorkStateEvent
}

object WorkStateEventFactoryForWorkScreen : WorkStateEventFactory<WorkEvent> {
	override fun running(status: LocalizedString, progressData: ProgressData) = WorkStateEvent.Running(status, progressData)
	override fun failed(reason: LocalizedString, stackTrace: String) = WorkStateEvent.Failed(reason, stackTrace)
	override fun succeeded() = WorkStateEvent.Succeeded
	override fun canceled() = WorkStateEvent.Canceled
	override fun unknown() = WorkStateEvent.Unknown
}

sealed interface WorkEffect : JetEffect