package ru.solrudev.okkeipatcher.ui.navhost

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import io.github.solrudev.jetmvi.HostJetView
import io.github.solrudev.jetmvi.derivedView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.OkkeiApplication
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.OkkeiNavHostBinding
import ru.solrudev.okkeipatcher.ui.navhost.controller.NavigationController
import ru.solrudev.okkeipatcher.ui.navhost.controller.ThemeController
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(R.layout.okkei_nav_host), HostJetView<NavHostUiState> {

	private val binding by viewBinding(OkkeiNavHostBinding::bind, R.id.container_nav_host)
	private val navigationController by derivedView { NavigationController(navController, viewModel) }
	private val themeController by derivedView { ThemeController(application as OkkeiApplication) }
	private val topLevelDestinations = setOf(R.id.main_fragment, R.id.work_fragment, R.id.permissions_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	private val viewModel: NavHostViewModel by jetViewModels(
		NavHostActivity::navigationController, NavHostActivity::themeController
	)

	private val navController: NavController
		get() = binding.contentNavHost.getFragment<NavHostFragment>().navController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		WindowCompat.setDecorFitsSystemWindows(window, false)
		with(binding) {
			setContentView(root)
			applyInsets()
			toolbarNavHost.setupWithNavController(navController, appBarConfiguration)
		}
	}

	override fun onStart() {
		super.onStart()
		viewModel.dispatchEvent(PermissionsCheckRequested)
	}

	private fun applyInsets(): Unit = with(binding) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			containerNavHost.fitsSystemWindows = true
			appBarLayoutNavHost.fitsSystemWindows = true
			return
		}
		appBarLayoutNavHost.applyInsetter {
			type(statusBars = true) {
				padding()
			}
			type(navigationBars = true) {
				margin(horizontal = true)
			}
		}
		contentNavHost.applyInsetter {
			type(navigationBars = true) {
				margin(horizontal = true)
			}
		}
	}
}