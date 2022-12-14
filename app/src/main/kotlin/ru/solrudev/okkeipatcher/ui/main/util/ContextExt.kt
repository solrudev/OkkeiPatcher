package ru.solrudev.okkeipatcher.ui.main.util

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.material.color.MaterialColors

/**
 * Utility extension method for [MaterialColors.getColor].
 */
fun Context.getMaterialColor(@AttrRes colorAttributeResId: Int, @ColorInt defaultValue: Int): Int {
	return MaterialColors.getColor(this, colorAttributeResId, defaultValue)
}