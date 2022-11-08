package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface ClearDataEvent : JetEvent {
	object WarningShown : ClearDataEvent
	object WarningDismissed : ClearDataEvent
	object ClearingRequested : ClearDataEvent, ClearDataEffect
	object DataCleared : ClearDataEvent
	data class ClearingFailed(val error: LocalizedString) : ClearDataEvent
	object ErrorMessageShown : ClearDataEvent
	object ViewHidden : ClearDataEvent
}

sealed interface ClearDataEffect : JetEffect