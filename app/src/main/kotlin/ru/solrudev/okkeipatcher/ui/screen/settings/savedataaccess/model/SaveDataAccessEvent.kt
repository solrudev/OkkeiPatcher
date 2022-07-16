package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model

import ru.solrudev.okkeipatcher.ui.core.Effect
import ru.solrudev.okkeipatcher.ui.core.Event

sealed interface SaveDataAccessEvent : Event {
	object RationaleShown : SaveDataAccessEvent
	object RationaleDismissed : SaveDataAccessEvent
	object PermissionGranted : SaveDataAccessEvent, SaveDataAccessEffect
	object HandleSaveDataEnabled : SaveDataAccessEvent
	object ViewHidden : SaveDataAccessEvent
}

sealed interface SaveDataAccessEffect : Effect