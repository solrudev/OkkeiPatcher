package ru.solrudev.okkeipatcher.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.getSystemService
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.core.resolve
import ru.solrudev.okkeipatcher.domain.core.Message

fun Context.copyTextToClipboard(clipLabel: CharSequence, text: CharSequence) {
	val clipboard = getSystemService<ClipboardManager>()
	clipboard?.setPrimaryClip(ClipData.newPlainText(clipLabel, text))
	Toast.makeText(this, R.string.toast_copied_to_clipboard, Toast.LENGTH_SHORT).show()
}

/**
 * Utility extension method for [MaterialColors.getColor].
 */
fun Context.getMaterialColor(@AttrRes colorAttributeResId: Int, @ColorInt defaultValue: Int): Int {
	return MaterialColors.getColor(this, colorAttributeResId, defaultValue)
}

/**
 * Creates a [MaterialAlertDialogBuilder] from a [Message].
 */
fun Context.createDialogBuilder(message: Message): MaterialAlertDialogBuilder {
	val titleString = message.title.resolve(this)
	val messageString = message.text.resolve(this)
	return MaterialAlertDialogBuilder(
		this,
		com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
	)
		.setCancelable(true)
		.setTitle(titleString)
		.setMessage(messageString)
}