package ru.solrudev.okkeipatcher.ui.main.screen.update.model

import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

sealed interface UpdateStatus {
	object NoUpdate : UpdateStatus
	object UpdateAvailable : UpdateStatus
	data class Downloading(val progressData: ProgressData) : UpdateStatus
	object AwaitingInstallation : UpdateStatus
	object Installing : UpdateStatus
	data class Failed(val reason: LocalizedString) : UpdateStatus
	object Canceled : UpdateStatus
	object Unknown : UpdateStatus
}