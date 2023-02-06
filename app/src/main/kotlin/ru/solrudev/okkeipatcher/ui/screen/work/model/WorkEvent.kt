package ru.solrudev.okkeipatcher.ui.screen.work.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface WorkEvent : JetEvent {
	data class CancelWork(val work: Work) : WorkEvent, WorkEffect
	data class StartObservingWork(val work: Work) : ObserveWorkEvent, WorkEffect
	object CancelRequested : WorkEvent
	object CancelMessageShown : WorkEvent
	object CancelMessageDismissed : WorkEvent
	object ErrorShown : WorkEvent
	object ErrorDismissed : WorkEvent
	object AnimationsPlayed : WorkEvent
	object ViewHidden : ObserveWorkEvent
}

sealed interface WorkStateEvent : WorkEvent {
	data class Running(val status: LocalizedString, val progressData: ProgressData) : WorkStateEvent
	data class Failed(val reason: LocalizedString, val stackTrace: String) : WorkStateEvent
	object Succeeded : WorkStateEvent
	object Canceled : WorkStateEvent
	object Unknown : WorkStateEvent
}

sealed interface ObserveWorkEvent : WorkEvent
sealed interface WorkEffect : JetEffect