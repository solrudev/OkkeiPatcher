package ru.solrudev.okkeipatcher.ui.util.extension

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.transition.MaterialSharedAxis

/**
 * Sets shared axis transitions for a Fragment.
 */
fun Fragment.setupTransitions() {
	enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
	returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
	exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
	reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
}

/**
 * Prepare the Fragment menu host's options menu to be displayed. Should be called from [Fragment.onViewCreated].
 * @param action action to be executed on the options menu. It is called with menu as its `this` receiver.
 */
inline fun Fragment.prepareOptionsMenu(crossinline action: Menu.() -> Unit) {
	val menuHost = requireActivity() as MenuHost
	menuHost.addMenuProvider(object : MenuProvider {
		override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) = menu.action()

		override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
			val navController = findNavController()
			return menuItem.onNavDestinationSelected(navController)
		}
	}, viewLifecycleOwner, Lifecycle.State.STARTED)
}

/**
 * Installs [OnBackPressedCallback] to the host Activity.
 */
inline fun Fragment.onBackPressed(crossinline action: () -> Unit) {
	val callback = object : OnBackPressedCallback(true) {
		override fun handleOnBackPressed() = action()
	}
	requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}