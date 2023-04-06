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