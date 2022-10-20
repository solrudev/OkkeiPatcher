package ru.solrudev.okkeipatcher.ui.util

import android.animation.LayoutTransition
import android.view.ViewGroup

/**
 * Enables [LayoutTransition.CHANGING] for this [ViewGroup]'s layout transition.
 */
fun ViewGroup.animateLayoutChanges() {
	layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)
}