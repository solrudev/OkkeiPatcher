/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.app.usecase.work.GetIsWorkPendingFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.RefreshRequested
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchUpdatesLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.Patched
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchStatus.UpdateAvailable
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PersistentPatchStatus
import javax.inject.Inject

class CheckPatchUpdatesMiddleware @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val getIsWorkPendingFlowUseCase: GetIsWorkPendingFlowUseCase,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : JetMiddleware<HomeEvent> {

	override fun MiddlewareScope<HomeEvent>.apply() {
		var canLoadPatchUpdates = false
		filterIsInstance<PatchStatusChanged>()
			.map { event -> event.patchStatus }
			.filterIsInstance<PersistentPatchStatus>()
			.combine(getIsWorkPendingFlowUseCase()) { patchStatus, isWorkPending ->
				val canLoadUpdates = !isWorkPending && patchStatus is Patched
				canLoadPatchUpdates = canLoadUpdates
				return@combine canLoadUpdates
			}
			.filter { it }
			.map { getPatchUpdatesUseCase(refresh = true) }
			.onEach { send(PatchUpdatesLoaded) }
			.filter { it.available }
			.onEach { send(PatchStatusChanged(UpdateAvailable)) }
			.launchIn(this)
		filterIsInstance<RefreshRequested>()
			.transform { if (canLoadPatchUpdates) emit(it) else send(PatchUpdatesLoaded) }
			.map { PersistentPatchStatus.of(getPatchStatusFlowUseCase().first()) }
			.onEach { patchStatus -> send(PatchStatusChanged(patchStatus)) }
			.launchIn(this)
	}
}