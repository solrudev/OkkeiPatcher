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

package ru.solrudev.okkeipatcher.ui.util

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.ui.navhost.NavHostActivity

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

fun Fragment.findNavHostToolbar(): Toolbar? = requireActivity().findViewById(R.id.toolbar_nav_host)
fun Fragment.requireNavHostActivity() = requireActivity() as NavHostActivity

fun Fragment.performHapticContextClick() {
	requireActivity()
		.findViewById<View>(android.R.id.content)
		.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
}