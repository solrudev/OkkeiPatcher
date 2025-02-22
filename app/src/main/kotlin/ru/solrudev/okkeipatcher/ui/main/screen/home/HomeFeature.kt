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

package ru.solrudev.okkeipatcher.ui.main.screen.home

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.EnqueuePatchWorkMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.EnqueueRestoreWorkMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.GetPatchSizeMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.ObservePatchVersionMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.PatchStatusMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.reducer.HomeReducer
import javax.inject.Inject

@ViewModelScoped
class HomeFeature @Inject constructor(
	observePatchVersionMiddleware: ObservePatchVersionMiddleware,
	patchStatusMiddleware: PatchStatusMiddleware,
	enqueuePatchWorkMiddleware: EnqueuePatchWorkMiddleware,
	enqueueRestoreWorkMiddleware: EnqueueRestoreWorkMiddleware,
	getPatchSizeMiddleware: GetPatchSizeMiddleware,
	homeReducer: HomeReducer
) : JetFeature<HomeEvent, HomeUiState>(
	middlewares = listOf(
		observePatchVersionMiddleware,
		patchStatusMiddleware,
		enqueuePatchWorkMiddleware,
		enqueueRestoreWorkMiddleware,
		getPatchSizeMiddleware
	),
	reducer = homeReducer,
	initialUiState = HomeUiState()
)