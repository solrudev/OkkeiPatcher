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

package ru.solrudev.okkeipatcher.ui.widget

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardCollapsingBinding
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges

private val DEF_STYLE_RES = com.google.android.material.R.style.Widget_MaterialComponents_CardView

/**
 * [MaterialCardView] with a title and collapsing content.
 */
class CollapsingCardView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr) {

	/**
	 * Indicates whether this card's content is expanded.
	 */
	var isExpanded = false
		private set

	private val binding: CardCollapsingBinding

	init {
		LayoutInflater.from(context).inflate(R.layout.card_collapsing, this, true)
		binding = CardCollapsingBinding.bind(this)
		parseAttributes(attrs, defStyleAttr)
		animateLayoutChanges()
		binding.containerCardCollapsing.animateLayoutChanges()
		setOnClickListener {
			setExpanded(!isExpanded)
		}
	}

	/**
	 * Sets this card's content visibility.
	 * @param expanded whether this card is expanded, i.e. is its content visible.
	 * @param animateArrow whether to animate arrow rotation.
	 */
	fun setExpanded(expanded: Boolean, animateArrow: Boolean = true) {
		binding.contentCardCollapsing.isVisible = expanded
		rotateArrow(expanded, animate = animateArrow)
		isExpanded = expanded
	}

	override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
		if (child?.id == R.id.container_card_collapsing) {
			super.addView(child, index, params)
			return
		}
		binding.contentCardCollapsing.addView(child, index, params)
	}

	override fun onSaveInstanceState(): Parcelable = SavedState(super.onSaveInstanceState()).also { state ->
		state.isExpanded = isExpanded
	}

	override fun onRestoreInstanceState(state: Parcelable?) {
		if (state !is SavedState) {
			super.onRestoreInstanceState(state)
			return
		}
		super.onRestoreInstanceState(state.superState)
		setExpanded(state.isExpanded, animateArrow = false)
	}

	private fun parseAttributes(attrs: AttributeSet?, defStyleAttr: Int) = with(binding) {
		attrs ?: return
		context.withStyledAttributes(attrs, R.styleable.CollapsingCardView, defStyleAttr) {
			textviewCardCollapsingTitle.text = getString(R.styleable.CollapsingCardView_title)
			val expand = getBoolean(R.styleable.CollapsingCardView_expand, false)
			setExpanded(expand, animateArrow = false)
		}
	}

	private fun rotateArrow(expand: Boolean, animate: Boolean) = with(binding.imageviewCardCollapsingArrow) {
		val degree = if (expand) 180f else 0f
		if (animate) {
			animate().rotation(degree).setDuration(200).start()
		} else {
			rotation = degree
		}
	}

	private class SavedState : BaseSavedState {

		var isExpanded = false

		constructor(superState: Parcelable?) : super(superState)

		constructor(source: Parcel) : super(source) {
			readParcel(source)
		}

		@RequiresApi(Build.VERSION_CODES.N)
		constructor(source: Parcel, classLoader: ClassLoader?) : super(source, classLoader) {
			readParcel(source)
		}

		override fun writeToParcel(out: Parcel, flags: Int) {
			super.writeToParcel(out, flags)
			out.writeInt(isExpanded.compareTo(false))
		}

		private fun readParcel(source: Parcel) {
			isExpanded = source.readInt() != 0
		}

		companion object {

			@Suppress("UNUSED")
			@JvmField
			val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.ClassLoaderCreator<SavedState> {

				override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						return SavedState(source, loader)
					}
					return SavedState(source)
				}

				override fun createFromParcel(source: Parcel) = SavedState(source)
				override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
			}
		}
	}
}