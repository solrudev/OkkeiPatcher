package ru.solrudev.okkeipatcher.ui.host

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.featureViewModels
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent.*
import ru.solrudev.okkeipatcher.ui.host.model.HostUiState
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

private const val PREVIOUS_DESTINATION_ID = "PREVIOUS_DESTINATION_ID"

@AndroidEntryPoint
class HostActivity : AppCompatActivity(R.layout.okkei_nav_host), FeatureView<HostUiState> {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.okkei_nav_host_container)
	private val viewModel: HostViewModel by featureViewModels()
	private val topLevelDestinations = setOf(R.id.home_fragment, R.id.update_fragment, R.id.settings_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	private val navController: NavController
		get() = findNavController(R.id.okkei_nav_host_content)

	@IdRes
	private var previousDestinationId = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		val navController = binding.okkeiNavHostContent.getFragment<NavHostFragment>().navController
		binding.bottomNavView.setupWithNavController(navController)
		setupActionBarWithNavController(navController, appBarConfiguration)
		checkPermissionsWithNavController(navController)
		previousDestinationId = savedInstanceState?.getInt(PREVIOUS_DESTINATION_ID) ?: 0
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putInt(PREVIOUS_DESTINATION_ID, previousDestinationId)
	}

	override fun onSupportNavigateUp() = navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

	override fun render(uiState: HostUiState) {
		if (uiState.permissionsRequired) {
			navigateToPermissionsScreen()
		}
		if (uiState.pendingWork != null) {
			navigateToWorkScreen(uiState.pendingWork)
		}
	}

	private fun checkPermissionsWithNavController(navController: NavController) = lifecycleScope.launch {
		navController
			.currentBackStackEntryFlow
			.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
			.collect { navBackStackEntry ->
				val previousDestination = previousDestinationId
				val currentDestination = navBackStackEntry.destination.id
				if (shouldCheckPermissions(previousDestination, currentDestination)) {
					viewModel.dispatchEvent(PermissionsCheckRequested)
				}
				previousDestinationId = currentDestination
			}
	}

	private fun shouldCheckPermissions(@IdRes previousDestination: Int, @IdRes currentDestination: Int): Boolean {
		val noPreviousDestination = previousDestination == 0
		val returnedFromPermissionsScreen =
			previousDestination == R.id.permissions_activity && previousDestination != currentDestination
		val isStartDestination = currentDestination == navController.graph.startDestinationId
		return noPreviousDestination || returnedFromPermissionsScreen || isStartDestination
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
}