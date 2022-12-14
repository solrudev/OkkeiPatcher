package ru.solrudev.okkeipatcher.ui.main.util

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar
import ru.solrudev.okkeipatcher.R

fun Fragment.showSnackbar(view: View, @StringRes resId: Int, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_main)
	Snackbar.make(view, resId, duration).apply {
		bottomNavigationView?.let(::setAnchorView)
	}.show()
}

fun Fragment.showSnackbar(view: View, text: CharSequence, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_main)
	Snackbar.make(view, text, duration).apply {
		bottomNavigationView?.let(::setAnchorView)
	}.show()
}