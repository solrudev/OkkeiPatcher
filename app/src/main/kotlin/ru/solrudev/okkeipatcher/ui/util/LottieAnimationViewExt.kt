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

package ru.solrudev.okkeipatcher.ui.util

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

/**
 * Registers an animator listener which invokes [action] on animation end.
 */
inline fun LottieAnimationView.onAnimationEnd(crossinline action: () -> Unit) {
	addAnimatorListener(object : Animator.AnimatorListener {
		override fun onAnimationStart(animation: Animator) {}

		override fun onAnimationEnd(animation: Animator) {
			action()
			removeAllAnimatorListeners()
		}

		override fun onAnimationCancel(animation: Animator) {}
		override fun onAnimationRepeat(animation: Animator) {}
	})
}

/**
 * Sets animation from [fileName] to this [LottieAnimationView] with `repeatCount = 0`.
 * @param fileName animation asset file name in _assets/animations_ folder.
 * @param start whether to start playing animation or just display last frame.
 */
fun LottieAnimationView.setOneshotAnimation(fileName: String, start: Boolean) {
	setAnimation("animations/$fileName")
	repeatCount = 0
	if (start) {
		playAnimation()
	} else {
		progress = 1f
	}
}