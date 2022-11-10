package ru.solrudev.okkeipatcher.ui.navhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.derivedView
import io.github.solrudev.jetmvi.jetViewModels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.navhost.view.UpdateBadgeView
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(R.layout.okkei_nav_host), JetView<NavHostUiState> {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.container_nav_host)
	private val updateBadgeView by derivedView { UpdateBadgeView(binding) }
	private val viewModel: NavHostViewModel by jetViewModels(NavHostActivity::updateBadgeView)
	private val topLevelDestinations = setOf(R.id.home_fragment, R.id.update_fragment, R.id.settings_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	private val navController: NavController
		get() = findNavController(R.id.content_nav_host)

	override val trackedState = listOf(NavHostUiState::permissionsRequired, NavHostUiState::pendingWork)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		with(binding) {
			setContentView(root)
			setSupportActionBar(toolbarNavHost)
			containerNavHost.animateLayoutChanges()
			val navController = contentNavHost.getFragment<NavHostFragment>().navController
			bottomNavigationViewNavHost?.let {
				it.setupWithNavController(navController)
				launchBottomNavigationFlows(navController)
			}
			navigationRailViewNavHost?.setupWithNavController(navController)
			navigationViewNavHost?.setupWithNavController(navController)
			setupActionBarWithNavController(navController, appBarConfiguration)
		}
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(PermissionsCheckRequested)
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
	}

	override fun render(uiState: NavHostUiState) {
		if (uiState.permissionsRequired) {
			navigateToPermissionsScreen()
		}
		uiState.pendingWork?.let(::navigateToWorkScreen)
	}

	private fun navigateToPermissionsScreen() {
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigateSafely(toPermissionsScreen)
		viewModel.dispatchEvent(NavigatedToPermissionsScreen)
	}

	private fun navigateToWorkScreen(work: Work) {
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigateSafely(toWorkScreen)
		viewModel.dispatchEvent(NavigatedToWorkScreen)
	}

	private fun launchBottomNavigationFlows(navController: NavController) = lifecycleScope.launch {
		lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
			showBottomNavigationOnDestinationChanged(navController).launchIn(this)
			hideBottomNavigationOnNonTopLevelDestinations(navController).launchIn(this)
		}
	}

	private fun showBottomNavigationOnDestinationChanged(navController: NavController) = navController
		.currentBackStackEntryFlow
		.filterNot { it.destination is DialogFragmentNavigator.Destination }
		.onEach {
			binding.bottomNavigationViewNavHost?.let {
				val params = it.layoutParams as CoordinatorLayout.LayoutParams
				val behavior = params.behavior as BottomNavigationViewBehavior
				if (behavior.isScrolledDown) {
					behavior.ignoreScroll()
					behavior.slideUp(it)
				}
			}
		}

	private fun hideBottomNavigationOnNonTopLevelDestinations(navController: NavController) = navController
		.currentBackStackEntryFlow
		.filterNot { it.destination is DialogFragmentNavigator.Destination }
		.map { it.destination.id in topLevelDestinations }
		.distinctUntilChanged()
		.onEach { isTopLevelDestination ->
			binding.bottomNavigationViewNavHost?.isVisible = isTopLevelDestination
		}
}