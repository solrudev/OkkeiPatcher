package ru.solrudev.okkeipatcher.ui.util.extension

import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

fun Fragment.setupTransitions() {
	enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
	returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
	exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
	reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
}