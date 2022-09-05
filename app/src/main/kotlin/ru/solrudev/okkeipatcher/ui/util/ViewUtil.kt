package ru.solrudev.okkeipatcher.ui.util

import android.view.View
import com.google.android.material.color.MaterialColors

/**
 * Workaround for a bug in shared-axis transition when View contains a RecyclerView.
 */
fun fixRecyclerViewTransition(view: View) {
	val colorBackground = MaterialColors.getColor(view, android.R.attr.colorBackground)
	view.setBackgroundColor(colorBackground)
}