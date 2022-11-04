package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model

import io.github.solrudev.jetmvi.Effect
import io.github.solrudev.jetmvi.Event

sealed interface SaveDataAccessEvent : Event {
	object RationaleShown : SaveDataAccessEvent
	object RationaleDismissed : SaveDataAccessEvent
	object PermissionGranted : SaveDataAccessEvent, SaveDataAccessEffect
	object HandleSaveDataEnabled : SaveDataAccessEvent
	object ViewHidden : SaveDataAccessEvent
}

sealed interface SaveDataAccessEffect : Effect