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

package ru.solrudev.okkeipatcher.ui.navhost

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckSaveDataAccessMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.ObservePendingWorkMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.ObserveThemeMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.navhost.reducer.NavHostReducer
import javax.inject.Inject

@ViewModelScoped
class NavHostFeature @Inject constructor(
	checkPermissionsMiddleware: CheckPermissionsMiddleware,
	observePendingWorkMiddleware: ObservePendingWorkMiddleware,
	checkSaveDataAccessMiddleware: CheckSaveDataAccessMiddleware,
	observeThemeMiddleware: ObserveThemeMiddleware,
	navHostReducer: NavHostReducer
) : JetFeature<NavHostEvent, NavHostUiState>(
	middlewares = listOf(
		checkPermissionsMiddleware,
		observePendingWorkMiddleware,
		checkSaveDataAccessMiddleware,
		observeThemeMiddleware
	),
	reducer = navHostReducer,
	initialUiState = NavHostUiState()
)