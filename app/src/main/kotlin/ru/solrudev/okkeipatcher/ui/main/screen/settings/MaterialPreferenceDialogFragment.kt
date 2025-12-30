/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.ui.main.screen.settings

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.preference.PreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class MaterialPreferenceDialogFragment<T : PreferenceDialogFragmentCompat>(private val factory: () -> T) {

	fun T.onCreateDialogMaterial(): Dialog {
		val builder = MaterialAlertDialogBuilder(requireContext())
			.setTitle(preference.dialogTitle)
			.setIcon(preference.dialogIcon)
			.setPositiveButton(preference.positiveButtonText, this)
			.setNegativeButton(preference.negativeButtonText, this)
		val contentView = onCreateDialogView(requireContext())
		if (contentView != null) {
			onBindDialogView(contentView)
			builder.setView(contentView)
		} else {
			builder.setMessage(preference.dialogMessage)
		}
		onPrepareDialogBuilder(builder)
		val dialog = builder.create()
		if (needInputMethod()) {
			requestInputMethod(dialog)
		}
		return builder.create()
	}

	fun T.onClickMaterial(which: Int) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			requireActivity()
				.window
				.decorView
				.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
		}
	}

	fun newInstance(key: String?): T {
		val fragment = factory()
		val bundle = Bundle(1)
		bundle.putString("key", key)
		fragment.arguments = bundle
		return fragment
	}

	private fun T.onCreateDialogView(context: Context): View? {
		return onCreateDialogView.invoke(this, context) as View?
	}

	private fun T.onBindDialogView(view: View) {
		onBindDialogView.invoke(this, view)
	}

	private fun T.onPrepareDialogBuilder(builder: AlertDialog.Builder) {
		onPrepareDialogBuilder.invoke(this, builder)
	}

	private fun T.needInputMethod(): Boolean {
		return needInputMethod.invoke(this) as Boolean
	}

	private fun T.requestInputMethod(dialog: AlertDialog) {
		requestInputMethod.invoke(this, dialog)
	}
}

private val onCreateDialogView = PreferenceDialogFragmentCompat::class.java
	.getDeclaredMethod("onCreateDialogView", Context::class.java)
	.apply { isAccessible = true }

private val onBindDialogView = PreferenceDialogFragmentCompat::class.java
	.getDeclaredMethod("onBindDialogView", View::class.java)
	.apply { isAccessible = true }

private val onPrepareDialogBuilder = PreferenceDialogFragmentCompat::class.java
	.getDeclaredMethod("onPrepareDialogBuilder", AlertDialog.Builder::class.java)
	.apply { isAccessible = true }

private val needInputMethod = PreferenceDialogFragmentCompat::class.java
	.getDeclaredMethod("needInputMethod")
	.apply { isAccessible = true }

private val requestInputMethod = PreferenceDialogFragmentCompat::class.java
	.getDeclaredMethod("requestInputMethod", Dialog::class.java)
	.apply { isAccessible = true }