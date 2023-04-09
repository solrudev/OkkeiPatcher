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
	getWorkStateFlowUseCase, completeWorkUseCase, cancelWorkUseCase,
	WorkStateEventFactoryForWorkScreen,
	startEventClass = StartObservingWork::class,
	cancelEventClass = CancelWork::class
)