package ru.solrudev.okkeipatcher.ui.util

import android.widget.TextView
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.ui.widget.AbortButton

/**
 * Sets [LocalizedString] value to [TextView].
 */
var TextView.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)
	}

/**
 * Sets [LocalizedString] value to [AbortButton].
 */
var AbortButton.localizedText: LocalizedString?
	get() = LocalizedString.raw(text)
	set(value) {
		text = value?.resolve(context)?.toString() ?: ""
	}