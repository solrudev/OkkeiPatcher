package ru.solrudev.okkeipatcher.ui.main.navhost

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
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
import ru.solrudev.okkeipatcher.ui.main.util.updateMargins
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges
import ru.solrudev.okkeipatcher.ui.util.findParentNavController

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), HostJetView<MainUiState> {

	private val binding by viewBinding(FragmentMainBinding::bind, R.id.container_main)
	private val updateBadgeView by derivedView { UpdateBadgeView(binding) }
	private val viewModel: MainViewModel by viewModels()
	private val topLevelDestinations = setOf(R.id.home_fragment, R.id.update_fragment, R.id.settings_fragment)
	private val appBarConfiguration = AppBarConfiguration(topLevelDestinations)
	private var isContentBottomMarginApplied = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
		containerMain.animateLayoutChanges()
		val navController = contentMain.getFragment<NavHostFragment>().navController
		bottomNavigationViewMain?.let { bottomNavigationView ->
			bottomNavigationView.setupWithNavController(navController)
			launchBottomNavigationFlows(navController, findParentNavController())
		}
		navigationRailViewMain?.setupWithNavController(navController)
		navigationViewMain?.setupWithNavController(navController)
		val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar_nav_host)
		toolbar.setupWithNavController(navController, appBarConfiguration)
		viewModel.bindDerived(this@MainFragment, updateBadgeView)
	}

	private fun launchBottomNavigationFlows(
		navController: NavController,
		parentNavController: NavController?
	) = viewLifecycleOwner.lifecycleScope.launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			hideBottomNavigationOnNonTopLevelDestinationsFlow(navController).launchIn(this)
			if (parentNavController != null) {
				resetContentBottomMarginFlagWhenNavigatedAwayFlow(parentNavController).launchIn(this)
			}
		}
	}

	private fun hideBottomNavigationOnNonTopLevelDestinationsFlow(navController: NavController) = navController
		.currentBackStackEntryFlow
		.filterNot { it.destination is DialogFragmentNavigator.Destination }
		.map { it.destination.id in topLevelDestinations }
		.distinctUntilChanged()
		.onEach { isTopLevelDestination ->
			binding.bottomNavigationViewMain?.isVisible = isTopLevelDestination
			updateContentBottomMargin(isTopLevelDestination)
		}

	private fun updateContentBottomMargin(isBottomNavigationVisible: Boolean) {
		if (isContentBottomMarginApplied == isBottomNavigationVisible) {
			return
		}
		isContentBottomMarginApplied = isBottomNavigationVisible
		binding.contentMain.doOnPreDraw { content ->
			val bottomNavigationView = binding.bottomNavigationViewMain ?: return@doOnPreDraw
			val coefficient = if (isBottomNavigationVisible) 1 else -1
			content.updateMargins(bottom = content.marginBottom + bottomNavigationView.height * coefficient)
		}
	}

	private fun resetContentBottomMarginFlagWhenNavigatedAwayFlow(navController: NavController) = navController
		.currentBackStackEntryFlow
		.filterNot { it.destination.id == R.id.main_fragment }
		.onEach {
			isContentBottomMarginApplied = false
		}
}