package ru.solrudev.okkeipatcher.ui.util

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

/**
 * Installs [OnBackPressedCallback] to the host Activity.
 */
inline fun Fragment.onBackPressed(crossinline action: () -> Unit) {
	val callback = object : OnBackPressedCallback(true) {
		override fun handleOnBackPressed() = action()
	}
	requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}

fun Fragment.findParentNavController(): NavController? {
	var parent = parentFragment
	while (parent != null) {
		if (parent is NavHostFragment) {
			return parent.navController
		}
		parent = parent.parentFragment
	}
	return null
}