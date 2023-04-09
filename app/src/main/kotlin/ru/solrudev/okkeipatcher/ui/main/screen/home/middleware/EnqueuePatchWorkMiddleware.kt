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

package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.EnqueuePatchWorkUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.StartPatch
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.*
import javax.inject.Inject

class EnqueuePatchWorkMiddleware @Inject constructor(
	private val enqueuePatchWorkUseCase: EnqueuePatchWorkUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : JetMiddleware<HomeEvent> {

	override fun MiddlewareScope<HomeEvent>.apply() {
		onEvent<StartPatch> {
			enqueuePatchWorkUseCase()
			val isPatched = getPatchStatusFlowUseCase().first()
			val currentStatus = if (isPatched) Patched else NotPatched
			send(PatchStatusChanged(WorkStarted(currentStatus)))
		}
	}
}