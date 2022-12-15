package ru.solrudev.okkeipatcher.ui.main.navhost

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.HostJetView
import io.github.solrudev.jetmvi.bindDerived
import io.github.solrudev.jetmvi.derivedView
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentMainBinding
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import ru.solrudev.okkeipatcher.ui.main.navhost.view.UpdateBadgeView
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), HostJetView<MainUiState> {

	private val binding by viewBinding(FragmentMainBinding::bind, R.id.container_main)
	private val updateBadgeView by derivedView { UpdateBadgeView(binding) }
	private val viewModel: MainViewModel by viewModels()
	private val topLevelDestinations = setOf(R.id.home_fragment, R.id.update_fragment, R.id.settings_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
		containerMain.animateLayoutChanges()
		val navController = contentMain.getFragment<NavHostFragment>().navController
		bottomNavigationViewMain?.let {
			it.setupWithNavController(navController)
			launchBottomNavigationFlows(navController)
		}
		navigationRailViewMain?.setupWithNavController(navController)
		navigationViewMain?.setupWithNavController(navController)
		val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_nav_host)
		toolbar.setupWithNavController(navController, appBarConfiguration)
		viewModel.bindDerived(this@MainFragment, updateBadgeView)
	}

	private fun launchBottomNavigationFlows(navController: NavController) = viewLifecycleOwner.lifecycleScope.launch {
		viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
			showBottomNavigationOnDestinationChanged(navController).launchIn(this)
			hideBottomNavigationOnNonTopLevelDestinations(navController).launchIn(this)
		}
	}

	private fun showBottomNavigationOnDestinationChanged(navController: NavController) = navController
		.currentBackStackEntryFlow
		.filterNot { it.destination is DialogFragmentNavigator.Destination }
		.onEach {
			binding.bottomNavigationViewMain?.let {
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
			binding.bottomNavigationViewMain?.isVisible = isTopLevelDestination
		}
}