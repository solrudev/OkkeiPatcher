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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.annotation.AttrRes
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
 * Creates a [MaterialAlertDialogBuilder] from a [Message].
 */
fun Context.createDialogBuilder(message: Message): MaterialAlertDialogBuilder {
	val titleString = message.title.resolve(this)
	val messageString = message.text.resolve(this)
	return MaterialAlertDialogBuilder(this)
		.setCancelable(true)
		.setTitle(titleString)
		.setMessage(messageString)
}

/**
 * Utility extension method for [MaterialColors.getColor].
 */
fun Context.getMaterialColor(@AttrRes colorAttributeResId: Int): Int {
	return MaterialColors.getColor(this, colorAttributeResId, "")
}