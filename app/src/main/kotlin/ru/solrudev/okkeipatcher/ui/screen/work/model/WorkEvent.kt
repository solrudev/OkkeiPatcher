package ru.solrudev.okkeipatcher.ui.screen.work.model

import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.ProgressData
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface WorkEvent : Event {
	data class CancelWork(val work: Work) : WorkEvent, WorkEffect
	data class StartObservingWork(val work: Work) : WorkEvent, WorkEffect
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
	data class Failed(val stackTrace: String) : WorkStateEvent
	object Succeeded : WorkStateEvent
	object Canceled : WorkStateEvent
	object Unknown : WorkStateEvent
}

sealed interface WorkEffect : Effect