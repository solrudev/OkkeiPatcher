package ru.solrudev.okkeipatcher.ui.util

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar
import ru.solrudev.okkeipatcher.R

fun Fragment.showSnackbar(view: View, @StringRes resId: Int, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_nav_host)
	Snackbar.make(view, resId, duration).apply {
		if (bottomNavigationView != null) {
			anchorView = bottomNavigationView
		}
	}.show()
}

fun Fragment.showSnackbar(view: View, text: CharSequence, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_nav_host)
	Snackbar.make(view, text, duration).apply {
		if (bottomNavigationView != null) {
			anchorView = bottomNavigationView
		}
	}.show()
}