package ru.solrudev.okkeipatcher.ui.util

import android.widget.TextView
import androidx.annotation.StringRes
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.isProgressActive
import com.github.razir.progressbutton.showProgress
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

/**
 * Set visibility of circular loading progress indicator displaying instead of text.
 * @param value Progress indicator visibility.
 * @param text Button text.
 */
fun TextView.setLoading(value: Boolean, @StringRes text: Int) {
	if (value) {
		showProgress { progressColor = currentTextColor }
	} else if (isProgressActive()) {
		hideProgress(text)
	}
}

/**
 * Sets [LocalizedString] value to [TextView].
 */
var TextView.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)
	}