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

package ru.solrudev.okkeipatcher.ui.main.screen.home.view

import androidx.lifecycle.LifecycleOwner
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.CardActionsBinding
import ru.solrudev.okkeipatcher.ui.main.screen.home.HomeViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchRequested
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.RestoreEvent.RestoreRequested
import ru.solrudev.okkeipatcher.ui.main.util.setLoading
import ru.solrudev.okkeipatcher.ui.main.util.setupProgressButton

class ActionsView(
	lifecycleOwner: LifecycleOwner,
	private val binding: CardActionsBinding,
	private val viewModel: HomeViewModel
) : JetView<HomeUiState> {

	init {
		binding.buttonCardActionsPatch.setupProgressButton(lifecycleOwner)
		setupNavigation()
	}

	override val trackedState = listOf(
		HomeUiState::isPatchEnabled,
		HomeUiState::isRestoreEnabled,
		HomeUiState::isPatchSizeLoading
	)

	override fun render(uiState: HomeUiState) = with(binding) {
		buttonCardActionsPatch.isEnabled = uiState.isPatchEnabled
		buttonCardActionsRestore.isEnabled = uiState.isRestoreEnabled
		buttonCardActionsPatch.setLoading(uiState.isPatchSizeLoading, R.string.button_text_patch)
	}

	private fun setupNavigation() = with(binding) {
		buttonCardActionsPatch.setOnClickListener {
			viewModel.dispatchEvent(PatchRequested)
		}
		buttonCardActionsRestore.setOnClickListener {
			viewModel.dispatchEvent(RestoreRequested)
		}
	}
}