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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isInvisible
import com.google.android.material.imageview.ShapeableImageView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.LayoutProgressIconBinding

/**
 * Icon with ability to display circular progress around it.
 */
class ProgressIcon : ConstraintLayout {

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_progress_icon, this, true)
		binding = LayoutProgressIconBinding.bind(this)
	}

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
		context, attrs, defStyleAttr, defStyleRes
	) {
		parseAttributes(attrs, defStyleAttr, defStyleRes)
	}

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		parseAttributes(attrs, defStyleAttr, 0)
	}

	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
		parseAttributes(attrs, 0, 0)
	}

	constructor(context: Context) : super(context)

	/**
	 * Returns circular progress indicator visibility.
	 */
	var isProgressVisible = false
		private set

	private val binding: LayoutProgressIconBinding

	var progress by binding.progressCircularProgressIcon::progress
	var max by binding.progressCircularProgressIcon::max

	var isIndeterminate: Boolean
		get() = binding.progressCircularProgressIcon.isIndeterminate
		set(value) {
			binding.progressCircularProgressIcon.isIndeterminate = value
		}

	/**
	 * Sets circular progress indicator visibility.
	 * @param progressVisible progress indicator visibility.
	 * @param animate whether to animate state transition.
	 */
	fun setProgressVisible(progressVisible: Boolean, animate: Boolean = true) {
		if (isProgressVisible == progressVisible) {
			return
		}
		if (progressVisible) {
			showProgress(animate)
		} else {
			hideProgress(animate)
		}
		isProgressVisible = progressVisible
	}

	/**
	 * Sets the current progress to the specified value with/without animation based on the input.
	 *
	 * If it's in the indeterminate mode, it will smoothly transition to determinate mode by
	 * finishing the current indeterminate animation cycle.
	 *
	 * @param progress The new progress value.
	 * @param animated Whether to update the progress with the animation.
	 */
	fun setProgressCompat(progress: Int, animated: Boolean) {
		binding.progressCircularProgressIcon.setProgressCompat(progress, animated)
	}

	private fun showProgress(animate: Boolean) = with(binding) {
		progressCircularProgressIcon.run {
			if (animate) {
				alpha = 0f
				animate()
					.alpha(1f)
					.setListener(null)
					.setDuration(100)
					.setInterpolator(DecelerateInterpolator())
					.start()
			}
			isInvisible = false
		}
		imageviewProgressIcon.run {
			if (animate) {
				animateIcon(scale = 0.56f)
			} else {
				scaleX = 0.56f
				scaleY = 0.56f
			}
		}
	}

	private fun hideProgress(animate: Boolean) = with(binding) {
		progressCircularProgressIcon.run {
			if (animate) {
				animate()
					.alpha(0f)
					.setListener(object : AnimatorListenerAdapter() {
						override fun onAnimationEnd(animation: Animator) {
							isInvisible = true
							alpha = 1f
						}
					})
					.setDuration(100)
					.setInterpolator(DecelerateInterpolator())
					.start()
			} else {
				isInvisible = true
			}
		}
		imageviewProgressIcon.run {
			if (animate) {
				animateIcon(scale = 1f)
			} else {
				scaleX = 1f
				scaleY = 1f
			}
		}
	}

	private fun ShapeableImageView.animateIcon(scale: Float) {
		animate()
			.scaleX(scale)
			.scaleY(scale)
			.setDuration(200)
			.setInterpolator(DecelerateInterpolator())
			.start()
	}

	private fun parseAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) = with(binding) {
		attrs ?: return
		context.withStyledAttributes(attrs, R.styleable.ProgressIcon, defStyleAttr, defStyleRes) {
			progressCircularProgressIcon.indicatorSize = getDimensionPixelSize(R.styleable.ProgressIcon_size, 0)
			val iconSrc = getResourceId(R.styleable.ProgressIcon_iconSrc, R.color.color_app_icon_placeholder)
			imageviewProgressIcon.setImageResource(iconSrc)
			val isProgressVisible = getBoolean(R.styleable.ProgressIcon_progressVisible, false)
			setProgressVisible(isProgressVisible, animate = false)
			progressCircularProgressIcon.run {
				progress = getInteger(R.styleable.ProgressIcon_progress, progress)
				max = getInteger(R.styleable.ProgressIcon_max, max)
				isIndeterminate = getBoolean(R.styleable.ProgressIcon_indeterminate, isIndeterminate)
			}
		}
	}
}