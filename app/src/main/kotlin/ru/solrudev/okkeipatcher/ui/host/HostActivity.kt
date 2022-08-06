package ru.solrudev.okkeipatcher.ui.host

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.OkkeiContainerBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.core.FeatureView
import ru.solrudev.okkeipatcher.ui.core.renderBy
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent.*
import ru.solrudev.okkeipatcher.ui.host.model.HostUiState

private const val PREVIOUS_DESTINATION_ID = "PREVIOUS_DESTINATION_ID"

@AndroidEntryPoint
class HostActivity : AppCompatActivity(R.layout.okkei_container), FeatureView<HostUiState> {

	private val binding by viewBinding(OkkeiContainerBinding::bind, R.id.okkei_container)
	private val viewModel by viewModels<HostViewModel>()

	private val appBarConfiguration = AppBarConfiguration(
		setOf(R.id.home_fragment, R.id.permissions_fragment, R.id.work_fragment)
	)

	private val navController: NavController
		get() = findNavController(R.id.okkei_nav_host_content)

	@IdRes
	private var previousDestinationId = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		with(binding.okkeiContent) {
			setSupportActionBar(toolbar)
			val navController = okkeiNavHostContent.getFragment<NavHostFragment>().navController
			setupActionBarWithNavController(navController, appBarConfiguration)
			checkPermissionsWithNavController(navController)
		}
		setupOptionsMenu()
		viewModel.renderBy(this)
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

	private fun setupOptionsMenu() {
		addMenuProvider(object : MenuProvider {
			override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
				menuInflater.inflate(R.menu.okkei_menu, menu)

			override fun onMenuItemSelected(menuItem: MenuItem) = menuItem.onNavDestinationSelected(navController)
		})
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
			previousDestination == R.id.permissions_fragment && previousDestination != currentDestination
		val isStartDestination = currentDestination == navController.graph.startDestinationId
		return noPreviousDestination || returnedFromPermissionsScreen || isStartDestination
	}

	private fun navigateToPermissionsScreen() {
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigate(toPermissionsScreen)
		viewModel.dispatchEvent(NavigatedToPermissionsScreen)
	}

	private fun navigateToWorkScreen(work: Work) {
		val workScreen = navController.findDestination(R.id.work_fragment)
		workScreen?.label = work.label.resolve(this)
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigate(toWorkScreen)
		viewModel.dispatchEvent(NavigatedToWorkScreen)
	}
}