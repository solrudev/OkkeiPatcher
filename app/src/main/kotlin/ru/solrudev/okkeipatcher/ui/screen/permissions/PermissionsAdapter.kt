package ru.solrudev.okkeipatcher.ui.screen.permissions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.ItemPermissionBinding
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionUiState
import ru.solrudev.okkeipatcher.ui.util.localizedText

class PermissionsAdapter(
	private val onButtonClick: (PermissionUiState) -> Unit
) : ListAdapter<PermissionUiState, PermissionsAdapter.PermissionViewHolder>(PermissionDiffCallback) {

	class PermissionViewHolder(
		itemView: View,
		private val onButtonClick: (PermissionUiState) -> Unit
	) : RecyclerView.ViewHolder(itemView) {

		private val binding = ItemPermissionBinding.bind(itemView)
		private var permission: PermissionUiState? = null

		init {
			binding.buttonPermissionGrant.setOnClickListener {
				permission?.let(onButtonClick)
			}
		}

		fun bind(uiState: PermissionUiState) {
			this.permission = uiState
			binding.textViewPermissionTitle.localizedText = uiState.permission.title
			binding.textViewPermissionDescription.localizedText = uiState.permission.description
			binding.buttonPermissionGrant.isEnabled = !uiState.isGranted
			val buttonText = if (uiState.isGranted) R.string.granted else R.string.grant
			binding.buttonPermissionGrant.setText(buttonText)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_permission, parent, false)
		return PermissionViewHolder(view, onButtonClick)
	}

	override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
		val permission = getItem(position)
		holder.bind(permission)
	}
}

private object PermissionDiffCallback : DiffUtil.ItemCallback<PermissionUiState>() {

	override fun areItemsTheSame(oldItem: PermissionUiState, newItem: PermissionUiState) =
		oldItem.permission.id == newItem.permission.id

	override fun areContentsTheSame(oldItem: PermissionUiState, newItem: PermissionUiState) = oldItem == newItem
}