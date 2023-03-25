package ru.solrudev.okkeipatcher.ui.main.screen.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.HostJetView
import io.github.solrudev.jetmvi.derivedView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentHomeBinding
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent.ViewHidden
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.view.*
import ru.solrudev.okkeipatcher.ui.util.animateLayoutChanges

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), HostJetView<HomeUiState> {

	private val binding by viewBinding(FragmentHomeBinding::bind)

	private val gameInfoView by derivedView {
		GameInfoView(binding.cardHomeGameInfo)
	}

	private val actionsView by derivedView {
		ActionsView(viewLifecycleOwner, binding.cardHomeActions, viewModel)
	}

	private val patchStatusView by derivedView {
		PatchStatusView(binding.cardHomePatchStatus)
	}

	private val patchMessageView by derivedView {
		PatchMessageView(requireContext(), viewLifecycleOwner.lifecycle, viewModel)
	}

	private val restoreMessageView by derivedView {
		RestoreMessageView(requireContext(), viewLifecycleOwner.lifecycle, viewModel)
	}

	private val viewModel: HomeViewModel by jetViewModels(
		HomeFragment::gameInfoView,
		HomeFragment::actionsView,
		HomeFragment::patchStatusView,
		HomeFragment::patchMessageView,
		HomeFragment::restoreMessageView
	)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		setupNavigation()
		containerHome.animateLayoutChanges()
	}

	override fun onStop() {
		super.onStop()
		viewModel.dispatchEvent(ViewHidden)
	}

	private fun setupNavigation() = with(binding) {
		cardHomePatchStatus.buttonCardUpdate.setOnClickListener {
			cardHomeActions.buttonCardActionsPatch.performClick()
		}
	}
}