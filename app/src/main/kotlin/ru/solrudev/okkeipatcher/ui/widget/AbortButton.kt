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
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import ru.solrudev.okkeipatcher.R

private val DEF_STYLE_RES = com.google.android.material.R.style.Widget_MaterialComponents_Button
private val STATE_ABORT_ENABLED = intArrayOf(R.attr.state_abort_enabled)

/**
 * [MaterialButton] which has an additional state of abort button.
 *
 * In abort state, theme's `?attr/colorAbort` is used for the background tint color and `?attr/colorOnAbort` is used
 * for the text and icon color.
 */
class AbortButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle
) : MaterialButton(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES), attrs, defStyleAttr) {

	/**
	 * Current abort state.
	 */
	var isAbortEnabled: Boolean = false
		private set

	/**
	 * Button text in abort state.
	 */
	var abortText: String
		get() = _abortText
		set(value) {
			if (_abortText != value) {
				_abortText = value
				if (isAbortEnabled) {
					setText(value)
				}
			}
		}

	/**
	 * Button text in normal state.
	 */
	var text: String
		get() = _text
		@JvmName("setNormalText") set(value) {
			if (_text != value) {
				_text = value
				if (!isAbortEnabled) {
					setText(value)
				}
			}
		}

	private var _abortText = ""
	private var _text = ""
	private var isInitialized = false

	init {
		parseAttributes(attrs, defStyleAttr)
	}

	/**
	 * Sets abort state for this button.
	 * @param abortEnabled abort state.
	 * @param animate whether to animate abort state transition.
	 */
	fun setAbortEnabled(abortEnabled: Boolean, animate: Boolean = true) {
		if (abortEnabled == isAbortEnabled && isInitialized) {
			return
		}
		if (animate) {
			animateAbortStateTransition()
		}
		if (abortEnabled) {
			setAbortText()
		} else {
			setNormalText()
		}
		isAbortEnabled = abortEnabled
		refreshDrawableState()
	}

	override fun onCreateDrawableState(extraSpace: Int): IntArray {
		val drawableState = super.onCreateDrawableState(extraSpace + 1)
		if (isAbortEnabled) {
			mergeDrawableStates(drawableState, STATE_ABORT_ENABLED)
		}
		return drawableState
	}

	private fun parseAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
		attrs ?: return
		context.withStyledAttributes(attrs, R.styleable.AbortButton, defStyleAttr, DEF_STYLE_RES) {
			_abortText = getString(R.styleable.AbortButton_abortText) ?: ""
			_text = getString(R.styleable.AbortButton_text) ?: ""
			val isAbortEnabled = getBoolean(R.styleable.AbortButton_abortEnabled, false)
			setAbortEnabled(isAbortEnabled, animate = false)
			isInitialized = true
		}
	}

	private fun animateAbortStateTransition() {
		alpha = 0f
		animate()
			.alpha(1f)
			.setDuration(500)
			.setInterpolator(DecelerateInterpolator())
			.start()
	}

	private fun setAbortText() {
		if (!isAbortEnabled || !isInitialized) {
			setText(_abortText)
		}
	}

	private fun setNormalText() {
		if (isAbortEnabled || !isInitialized) {
			setText(_text)
		}
	}
}