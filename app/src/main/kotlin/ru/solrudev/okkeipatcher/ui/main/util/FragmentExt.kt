/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.ui.main.util

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration
import com.google.android.material.snackbar.Snackbar
import ru.solrudev.okkeipatcher.R

fun Fragment.showSnackbar(@StringRes resId: Int, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_main)
	Snackbar.make(requireActivity().findViewById(R.id.content_main), resId, duration).apply {
		bottomNavigationView?.let(::setAnchorView)
	}.show()
}

fun Fragment.showSnackbar(text: CharSequence, @Duration duration: Int) {
	val bottomNavigationView = requireActivity().findViewById<View>(R.id.bottomNavigationView_main)
	Snackbar.make(requireActivity().findViewById(R.id.content_main), text, duration).apply {
		bottomNavigationView?.let(::setAnchorView)
	}.show()
}

fun Fragment.findNavigationBarView(): NavigationBarView? {
	return requireActivity().findViewById(R.id.bottomNavigationView_main)
		?: requireActivity().findViewById(R.id.navigationRailView_main)
}