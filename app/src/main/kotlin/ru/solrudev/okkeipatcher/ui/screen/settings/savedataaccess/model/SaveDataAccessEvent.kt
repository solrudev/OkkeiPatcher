package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent

sealed interface SaveDataAccessEvent : JetEvent {
	object RationaleShown : SaveDataAccessEvent
	object RationaleDismissed : SaveDataAccessEvent
	object PermissionGranted : SaveDataAccessEvent, SaveDataAccessEffect
	object HandleSaveDataEnabled : SaveDataAccessEvent
	object ViewHidden : SaveDataAccessEvent
}

sealed interface SaveDataAccessEffect : JetEffect