package ru.solrudev.okkeipatcher.ui.main.screen.licenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.License
import ru.solrudev.okkeipatcher.databinding.ItemLicenseBinding

class LicensesAdapter : ListAdapter<License, LicensesAdapter.LicenseViewHolder>(LicenseDiffCallback) {

	class LicenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		private val binding = ItemLicenseBinding.bind(itemView)

		fun bind(license: License) {
			binding.textviewLicenseSubject.text = license.subject
			binding.textviewLicenseText.text = license.text
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_license, parent, false)
		return LicenseViewHolder(view)
	}

	override fun onBindViewHolder(holder: LicenseViewHolder, position: Int) {
		val license = getItem(position)
		holder.bind(license)
	}
}

private object LicenseDiffCallback : DiffUtil.ItemCallback<License>() {
	override fun areItemsTheSame(oldItem: License, newItem: License) = oldItem.id == newItem.id
	override fun areContentsTheSame(oldItem: License, newItem: License) = oldItem == newItem
}