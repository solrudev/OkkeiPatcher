package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.CompleteWorkUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetWorkStateFlowUseCase
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.StartObservingWork
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkStateEventFactoryForWorkScreen
import ru.solrudev.okkeipatcher.ui.shared.middleware.WorkMiddleware
import javax.inject.Inject

class ObserveWorkMiddleware @Inject constructor(
	getWorkStateFlowUseCase: GetWorkStateFlowUseCase,
	completeWorkUseCase: CompleteWorkUseCase,
	cancelWorkUseCase: CancelWorkUseCase,
) : WorkMiddleware<WorkEvent, StartObservingWork, CancelWork, Nothing>(
	getWorkStateFlowUseCase,
	completeWorkUseCase,
	cancelWorkUseCase,
	WorkStateEventFactoryForWorkScreen,
	startEventClass = StartObservingWork::class,
	cancelEventClass = CancelWork::class
)