package ru.solrudev.okkeipatcher.ui.navhost.controller

import androidx.navigation.NavController
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.navhost.NavHostViewModel
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
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
		viewModel.dispatchEvent(NavHostEvent.NavigatedToPermissionsScreen)
		if (navController.currentDestination?.id == R.id.permissions_fragment) {
			return
		}
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigateSafely(toPermissionsScreen)
	}

	private fun navigateToWorkScreen(work: Work) {
		viewModel.dispatchEvent(NavHostEvent.NavigatedToWorkScreen)
		val workScreen = navController.findDestination(R.id.work_fragment)
		if (navController.currentDestination?.id == workScreen?.id) {
			return
		}
		workScreen?.label = work.label.resolve(navController.context)
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigateSafely(toWorkScreen)
	}
}