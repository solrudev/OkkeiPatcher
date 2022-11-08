package ru.solrudev.okkeipatcher.ui.screen.update

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.JetView
import io.github.solrudev.jetmvi.jetViewModels
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentUpdateBinding
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateUiState

@AndroidEntryPoint
class UpdateFragment : Fragment(R.layout.fragment_update), JetView<UpdateUiState> {

	private val viewModel: UpdateViewModel by jetViewModels()
	private val binding by viewBinding(FragmentUpdateBinding::bind)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (savedInstanceState == null) {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = false))
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupRefresh()
	}

	override fun render(uiState: UpdateUiState) {
		binding.swipeRefreshLayoutUpdate.isRefreshing = uiState.isLoading
	}

	private fun setupRefresh() {
		binding.swipeRefreshLayoutUpdate.setOnRefreshListener {
			viewModel.dispatchEvent(UpdateDataRequested(refresh = true))
		}
	}
}