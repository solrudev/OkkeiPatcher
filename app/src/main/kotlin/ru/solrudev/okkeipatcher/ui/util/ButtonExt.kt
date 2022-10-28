package ru.solrudev.okkeipatcher.ui.util

import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton

fun Button.showRippleEffect() {
	isPressed = true
	isPressed = false
}

/**
 * Binds button to [lifecycleOwner] and adds fade animations on progress display change.
 */
fun Button.setupProgressButton(lifecycleOwner: LifecycleOwner, fadeMillis: Long = 150) {
	lifecycleOwner.bindProgressButton(this)
	attachTextChangeAnimator {
		fadeInMills = fadeMillis
		fadeOutMills = fadeMillis
	}
}