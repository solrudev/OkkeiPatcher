package ru.solrudev.okkeipatcher.ui.view

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import ru.solrudev.okkeipatcher.R

private val DEF_STYLE_RES = com.google.android.material.R.style.Widget_MaterialComponents_Button
private val STATE_ABORT_ENABLED = intArrayOf(R.attr.state_abort_enabled)

/**
 * [MaterialButton] which has an additional state of abort button.
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
	 * Resource id of a text in abort state.
	 */
	var abortText: Int
		@StringRes get() = _abortText
		set(@StringRes value) {
			if (_abortText != value) {
				_abortText = value
				if (isAbortEnabled) {
					setText(value)
				}
			}
		}

	/**
	 * Resource id of a text in normal state.
	 */
	var text: Int
		@StringRes get() = _text
		@JvmName("setTextRes") set(@StringRes value) {
			if (_text != value) {
				_text = value
				if (!isAbortEnabled) {
					setText(value)
				}
			}
		}

	@StringRes
	private var _abortText = R.string.empty

	@StringRes
	private var _text = R.string.empty

	private var isInitialized = false

	init {
		parseAttributes(attrs, defStyleAttr)
	}

	/**
	 * Sets abort state for this button.
	 * @param abortEnabled abort state.
	 * @param animate animate abort state transition.
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
		context.withStyledAttributes(attrs, R.styleable.AbortButton, defStyleAttr, DEF_STYLE_RES) {
			_abortText = getResourceId(R.styleable.AbortButton_abortText, _abortText)
			_text = getResourceId(R.styleable.AbortButton_text, R.string.empty)
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