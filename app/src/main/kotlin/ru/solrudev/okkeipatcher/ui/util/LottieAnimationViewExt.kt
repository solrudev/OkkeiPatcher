package ru.solrudev.okkeipatcher.ui.util

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

/**
 * Registers an animator listener which invokes [action] on animation end.
 */
inline fun LottieAnimationView.onAnimationEnd(crossinline action: () -> Unit) {
	addAnimatorListener(object : Animator.AnimatorListener {
		override fun onAnimationStart(animation: Animator) {}
		override fun onAnimationEnd(animation: Animator) = action()
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