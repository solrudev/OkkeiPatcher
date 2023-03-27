package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetPendingUpdateDownloadWorkFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.CancelWork
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.StartObservingDownloadWork
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateWorkEventFactory
import ru.solrudev.okkeipatcher.ui.shared.middleware.WorkMiddleware
import javax.inject.Inject

class ObserveDownloadWorkMiddleware @Inject constructor(
	getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	completeWorkUseCase: CompleteWorkUseCase,
	cancelWorkUseCase: CancelWorkUseCase,
	getPendingUpdateDownloadWorkFlowUseCase: GetPendingUpdateDownloadWorkFlowUseCase
) : WorkMiddleware<UpdateEvent, StartObservingDownloadWork, CancelWork, StartObservingDownloadWork>(
	getWorkStateFlowUseCase, completeWorkUseCase, cancelWorkUseCase,
	UpdateWorkEventFactory,
	startEventClass = StartObservingDownloadWork::class,
	cancelEventClass = CancelWork::class,
	pendingWorkFlowProducer = getPendingUpdateDownloadWorkFlowUseCase::invoke,
	pendingWorkEventProducer = ::StartObservingDownloadWork
)