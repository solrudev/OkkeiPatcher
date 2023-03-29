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
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.AppInfoView
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.ChangelogView
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.RefreshView
import ru.solrudev.okkeipatcher.ui.main.screen.update.view.UpdateStatusView

@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update), HostJetView<UpdateUiState> {

	private val refreshView by derivedView {
		RefreshView(binding.swipeRefreshLayoutUpdate, viewModel)
	}

	private val appInfoView by derivedView {
		AppInfoView(binding.cardUpdateAppInfo)
	}

	private val updateStatusView by derivedView {
		UpdateStatusView(binding.cardUpdateStatus, viewModel)
	}

	private val changelogView by derivedView {
		ChangelogView(binding.textviewUpdateChangelog, binding.cardCollapsingChangelogContainerUpdate)
	}

	private val viewModel: UpdateViewModel by jetViewModels(
		UpdateFragment::refreshView,
		UpdateFragment::appInfoView,
		UpdateFragment::updateStatusView,
		UpdateFragment::changelogView
	)

	private val binding by viewBinding(FragmentUpdateBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (savedInstanceState == null) {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = false))
		}
	}
}