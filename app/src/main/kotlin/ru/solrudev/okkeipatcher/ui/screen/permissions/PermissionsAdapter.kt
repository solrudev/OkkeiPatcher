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

package ru.solrudev.okkeipatcher.ui.screen.permissions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.Permission
import ru.solrudev.okkeipatcher.databinding.ItemPermissionBinding
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionUiState
import ru.solrudev.okkeipatcher.ui.util.localizedText

class PermissionsAdapter(
	private val onButtonClick: (Permission) -> Unit
) : ListAdapter<PermissionUiState, PermissionsAdapter.PermissionViewHolder>(PermissionDiffCallback) {

	class PermissionViewHolder(
		itemView: View,
		private val onButtonClick: (Permission) -> Unit
	) : RecyclerView.ViewHolder(itemView) {

		private val binding = ItemPermissionBinding.bind(itemView)
		private var permissionUiState: PermissionUiState? = null

		init {
			binding.buttonPermissionGrant.setOnClickListener {
				permissionUiState?.let {
					onButtonClick(it.permission)
				}
			}
		}

		fun bind(uiState: PermissionUiState) {
			permissionUiState = uiState
			binding.textViewPermissionTitle.localizedText = uiState.permission.title
			binding.textViewPermissionDescription.localizedText = uiState.permission.description
			binding.buttonPermissionGrant.isEnabled = !uiState.isGranted
			val buttonText = if (uiState.isGranted) R.string.button_text_granted else R.string.button_text_grant
			binding.buttonPermissionGrant.setText(buttonText)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_permission, parent, false)
		return PermissionViewHolder(view, onButtonClick)
	}

	override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
		val permissionUiState = getItem(position)
		holder.bind(permissionUiState)
	}
}

private object PermissionDiffCallback : DiffUtil.ItemCallback<PermissionUiState>() {

	override fun areItemsTheSame(oldItem: PermissionUiState, newItem: PermissionUiState) =
		oldItem.permission.id == newItem.permission.id

	override fun areContentsTheSame(oldItem: PermissionUiState, newItem: PermissionUiState) = oldItem == newItem
}