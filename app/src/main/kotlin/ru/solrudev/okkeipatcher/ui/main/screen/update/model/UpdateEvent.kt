package ru.solrudev.okkeipatcher.ui.main.screen.update.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.OkkeiPatcherVersion
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.*
import ru.solrudev.okkeipatcher.ui.shared.model.HasWork
import ru.solrudev.okkeipatcher.ui.shared.model.WorkStateEventFactory

sealed interface UpdateEvent : JetEvent {
	object UpdateDataLoadingStarted : UpdateEvent
	data class UpdateDataRequested(val refresh: Boolean) : UpdateEvent, UpdateEffect
	data class UpdateDataLoaded(val size: Double, val changelog: List<OkkeiPatcherVersion>) : UpdateEvent
	data class UpdateStatusChanged(val updateStatus: UpdateStatus) : UpdateEvent
	object UpdateDownloadRequested : UpdateEvent, UpdateEffect
	object UpdateInstallRequested : UpdateEvent, UpdateEffect
	data class StartObservingDownloadWork(override val work: Work) : UpdateEvent, HasWork
	data class CancelWork(override val work: Work) : UpdateEvent, UpdateEffect, HasWork
}

object UpdateWorkEventFactory : WorkStateEventFactory<UpdateEvent> {
	override fun running(status: LocalizedString, progressData: ProgressData) = UpdateStatusChanged(Downloading(progressData))
	override fun failed(reason: LocalizedString, stackTrace: String) = UpdateStatusChanged(Failed(reason))
	override fun succeeded() = UpdateStatusChanged(AwaitingInstallation)
	override fun canceled() = UpdateStatusChanged(Canceled)
	override fun unknown() = UpdateStatusChanged(Unknown)
}

sealed interface UpdateEffect : JetEffect