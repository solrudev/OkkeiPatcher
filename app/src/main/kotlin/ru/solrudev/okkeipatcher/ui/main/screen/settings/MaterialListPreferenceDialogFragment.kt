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

package ru.solrudev.okkeipatcher.ui.main.screen.settings

import android.app.Dialog
import android.os.Bundle
import androidx.preference.ListPreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MaterialListPreferenceDialogFragment : ListPreferenceDialogFragmentCompat() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
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
		return builder.create()
	}

	companion object {
		fun newInstance(key: String?): MaterialListPreferenceDialogFragment {
			val fragment = MaterialListPreferenceDialogFragment()
			val bundle = Bundle(1)
			bundle.putString(ARG_KEY, key)
			fragment.arguments = bundle
			return fragment
		}
	}
}