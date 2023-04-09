/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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