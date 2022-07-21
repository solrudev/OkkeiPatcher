package ru.solrudev.okkeipatcher.ui.util

import android.view.View
import com.google.android.material.color.MaterialColors

/**
 * Workaround for a bug in shared-axis transition when View contains a RecyclerView.
 */
fun View.fixRecyclerViewTransition() {
	val colorBackground = MaterialColors.getColor(this, android.R.attr.colorBackground)
	setBackgroundColor(colorBackground)
}