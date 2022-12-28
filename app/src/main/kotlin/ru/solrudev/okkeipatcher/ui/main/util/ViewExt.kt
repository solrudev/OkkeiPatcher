package ru.solrudev.okkeipatcher.ui.main.util

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.Px

fun View.updateMargins(
	params: MarginLayoutParams? = layoutParams as? MarginLayoutParams,
	@Px left: Int = params?.leftMargin ?: 0,
	@Px top: Int = params?.topMargin ?: 0,
	@Px right: Int = params?.rightMargin ?: 0,
	@Px bottom: Int = params?.bottomMargin ?: 0
) {
	params ?: return
	params.setMargins(left, top, right, bottom)
	requestLayout()
}