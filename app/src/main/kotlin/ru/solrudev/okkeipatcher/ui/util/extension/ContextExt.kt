package ru.solrudev.okkeipatcher.ui.util.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.core.content.getSystemService
import ru.solrudev.okkeipatcher.R

fun Context.copyTextToClipboard(clipLabel: CharSequence, text: CharSequence) {
	val clipboard = getSystemService<ClipboardManager>()
	clipboard?.setPrimaryClip(ClipData.newPlainText(clipLabel, text))
	Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
}