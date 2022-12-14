package ru.solrudev.okkeipatcher.ui.util

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

/**
 * Installs [OnBackPressedCallback] to the host Activity.
 */
inline fun Fragment.onBackPressed(crossinline action: () -> Unit) {
	val callback = object : OnBackPressedCallback(true) {
		override fun handleOnBackPressed() = action()
	}
	requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
}