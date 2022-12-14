package ru.solrudev.okkeipatcher.ui.util

import android.widget.TextView
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString

/**
 * Sets [LocalizedString] value to [TextView].
 */
var TextView.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)
	}