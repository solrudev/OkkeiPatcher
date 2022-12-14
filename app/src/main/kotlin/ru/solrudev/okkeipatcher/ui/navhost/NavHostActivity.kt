package ru.solrudev.okkeipatcher.ui.navhost

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.OkkeiNavGraphDirections
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.domain.model.Work
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.*
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(R.layout.okkei_nav_host), JetView<NavHostUiState> {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.container_nav_host)
	private val viewModel: NavHostViewModel by jetViewModels()
	private val topLevelDestinations = setOf(R.id.main_fragment, R.id.work_fragment, R.id.permissions_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	private val navController: NavController
		get() = findNavController(R.id.content_nav_host)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		with(binding) {
			setContentView(root)
			containerNavHost.animateLayoutChanges()
			val navController = contentNavHost.getFragment<NavHostFragment>().navController
			toolbarNavHost.setupWithNavController(navController, appBarConfiguration)
		}
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(PermissionsCheckRequested)
	}

	override fun render(uiState: NavHostUiState) {
		if (uiState.permissionsRequired) {
			navigateToPermissionsScreen()
		}
		uiState.pendingWork?.let(::navigateToWorkScreen)
	}

	private fun navigateToPermissionsScreen() {
		if (navController.currentDestination?.id == R.id.permissions_fragment) {
			return
		}
		val toPermissionsScreen = OkkeiNavGraphDirections.actionGlobalPermissions()
		navController.navigateSafely(toPermissionsScreen)
		viewModel.dispatchEvent(NavigatedToPermissionsScreen)
	}

	private fun navigateToWorkScreen(work: Work) {
		val navController = navController
		val workScreen = navController.findDestination(R.id.work_fragment)
		if (navController.currentDestination?.id == workScreen?.id) {
			return
		}
		workScreen?.label = work.label.resolve(this)
		val toWorkScreen = OkkeiNavGraphDirections.actionGlobalWork(work)
		navController.navigateSafely(toWorkScreen)
		viewModel.dispatchEvent(NavigatedToWorkScreen)
	}
}