package ru.solrudev.okkeipatcher.ui.navhost

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(R.layout.okkei_nav_host), FeatureView<NavHostUiState> {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.container_nav_host)
	private val viewModel: NavHostViewModel by featureViewModels()
	private val topLevelDestinations = setOf(R.id.home_fragment, R.id.update_fragment, R.id.settings_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	private val navController: NavController
		get() = findNavController(R.id.content_nav_host)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbarNavHost)
		binding.containerNavHost.animateLayoutChanges()
		val navController = binding.contentNavHost.getFragment<NavHostFragment>().navController
		binding.bottomNavigationViewNavHost?.let {
			it.setupWithNavController(navController)
			showBottomNavigationOnDestinationChanged(navController)
			hideBottomNavigationOnNonTopLevelDestinations(navController)
		}
		binding.navigationRailViewNavHost?.setupWithNavController(navController)
		binding.navigationViewNavHost?.setupWithNavController(navController)
		setupActionBarWithNavController(navController, appBarConfiguration)
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(PermissionsCheckRequested)
	}

	override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

	override fun render(uiState: NavHostUiState) {
		if (uiState.permissionsRequired) {
			navigateToPermissionsScreen()
		}
		if (uiState.pendingWork != null) {
			navigateToWorkScreen(uiState.pendingWork)
		}
		displayUpdateBadge(uiState.isUpdateAvailable)
	}

	private fun navigateToPermissionsScreen() {
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigateSafely(toPermissionsScreen)
		viewModel.dispatchEvent(NavigatedToPermissionsScreen)
	}

	private fun navigateToWorkScreen(work: Work) {
		val workScreen = navController.findDestination(R.id.work_activity)
		workScreen?.label = work.label.resolve(this)
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigateSafely(toWorkScreen)
		viewModel.dispatchEvent(NavigatedToWorkScreen)
	}

	private fun showBottomNavigationOnDestinationChanged(navController: NavController) = navController
		.currentBackStackEntryFlow
		.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
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
		.launchIn(lifecycleScope)

	private fun displayUpdateBadge(isUpdateAvailable: Boolean) {
		if (isUpdateAvailable) {
			val color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorError, Color.RED)
			binding.bottomNavigationViewNavHost?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
			binding.navigationRailViewNavHost?.getOrCreateBadge(R.id.update_fragment)?.apply {
				backgroundColor = color
			}
		} else {
			binding.bottomNavigationViewNavHost?.removeBadge(R.id.update_fragment)
			binding.navigationRailViewNavHost?.removeBadge(R.id.update_fragment)
		}
	}

	private fun hideBottomNavigationOnNonTopLevelDestinations(navController: NavController) = navController
		.currentBackStackEntryFlow
		.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
		.filterNot { it.destination is DialogFragmentNavigator.Destination }
		.map { it.destination.id in topLevelDestinations }
		.distinctUntilChanged()
		.onEach { isTopLevelDestination ->
			binding.bottomNavigationViewNavHost?.isVisible = isTopLevelDestination
		}
		.launchIn(lifecycleScope)
}