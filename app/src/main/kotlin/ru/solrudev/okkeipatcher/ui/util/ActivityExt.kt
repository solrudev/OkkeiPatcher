package ru.solrudev.okkeipatcher.ui.util

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

/**
 * Installs [OnBackPressedCallback].
 */
inline fun ComponentActivity.onBackPressed(crossinline action: () -> Unit) {
	val callback = object : OnBackPressedCallback(true) {
		override fun handleOnBackPressed() = action()
	}
	onBackPressedDispatcher.addCallback(this, callback)
}