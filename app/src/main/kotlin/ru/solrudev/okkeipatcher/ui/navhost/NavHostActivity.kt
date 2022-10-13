package ru.solrudev.okkeipatcher.ui.navhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
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
		setSupportActionBar(binding.toolbar)
		val navController = binding.contentNavHost.getFragment<NavHostFragment>().navController
		binding.bottomNavigationViewNavHost?.let {
			it.setupWithNavController(navController)
			showBottomNavigationOnDestinationChanged(navController)
		}
		binding.navigationRailViewNavHost?.setupWithNavController(navController)
		binding.navigationViewNavHost?.setupWithNavController(navController)
		setupActionBarWithNavController(navController, appBarConfiguration)
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
}