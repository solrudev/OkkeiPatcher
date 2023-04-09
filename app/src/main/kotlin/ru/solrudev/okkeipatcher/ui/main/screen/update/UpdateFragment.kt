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

package ru.solrudev.okkeipatcher.ui.main.screen.update

import android.os.Bundle
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.HostJetView
import io.github.solrudev.jetmvi.derivedView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentUpdateBinding
import ru.solrudev.okkeipatcher.ui.main.screen.update.controller.RefreshController
import ru.solrudev.okkeipatcher.ui.main.screen.update.controller.UpdateButtonClickController
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.AppInfoView
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.ChangelogView
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.UpdateStatusView

@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update), HostJetView<UpdateUiState> {

	private val appInfoView by derivedView {
		AppInfoView(binding.cardUpdateAppInfo)
	}

	private val updateStatusView by derivedView {
		UpdateStatusView(binding.cardUpdateStatus)
	}

	private val changelogView by derivedView {
		ChangelogView(binding.textviewUpdateChangelog, binding.cardCollapsingChangelogContainerUpdate)
	}

	private val refreshController by derivedView {
		RefreshController(binding.swipeRefreshLayoutUpdate, viewModel)
	}

	private val updateButtonClickController by derivedView {
		UpdateButtonClickController(binding.cardUpdateStatus.buttonCardUpdate, viewModel)
	}

	private val viewModel: UpdateViewModel by jetViewModels(
		UpdateFragment::appInfoView,
		UpdateFragment::updateStatusView,
		UpdateFragment::changelogView,
		UpdateFragment::refreshController,
		UpdateFragment::updateButtonClickController
	)

	private val binding by viewBinding(FragmentUpdateBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (savedInstanceState == null) {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = false))
		}
	}
}