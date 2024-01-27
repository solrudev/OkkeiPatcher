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

package ru.solrudev.okkeipatcher.ui.navhost.controller

import android.annotation.SuppressLint
import androidx.navigation.NavController
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.Work
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.ui.navhost.NavHostViewModel
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.NavigatedToPermissionsScreen
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.NavigatedToWorkScreen
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

class NavigationController(
	private val navController: NavController,
	private val viewModel: NavHostViewModel
) : JetView<NavHostUiState> {

	override val trackedState = listOf(NavHostUiState::permissionsRequired, NavHostUiState::pendingWork)

	override fun render(uiState: NavHostUiState) {
		if (uiState.permissionsRequired) {
			navigateToPermissionsScreen()
		}
		uiState.pendingWork?.let(::navigateToWorkScreen)
	}

	private fun navigateToPermissionsScreen() {
		viewModel.dispatchEvent(NavigatedToPermissionsScreen)
		if (navController.currentDestination?.id == R.id.permissions_fragment) {
			return
		}
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigateSafely(toPermissionsScreen)
	}

	@SuppressLint("RestrictedApi")
	private fun navigateToWorkScreen(work: Work) {
		viewModel.dispatchEvent(NavigatedToWorkScreen)
		val workScreen = navController.findDestination(R.id.work_fragment)
		if (navController.currentDestination?.id == workScreen?.id) {
			return
		}
		workScreen?.label = work.label.resolve(navController.context)
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigateSafely(toWorkScreen)
	}
}