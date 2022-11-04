package ru.solrudev.okkeipatcher.ui.screen.licenses

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.solrudev.jetmvi.FeatureView
import io.github.solrudev.jetmvi.bind
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.databinding.FragmentLicensesBinding
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesUiState

@AndroidEntryPoint
class LicensesFragment : Fragment(R.layout.fragment_licenses), FeatureView<LicensesUiState> {

	private val binding by viewBinding(FragmentLicensesBinding::bind)
	private val viewModel: LicensesViewModel by viewModels()
	private val licensesAdapter = LicensesAdapter()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.recyclerviewLicenses.adapter = licensesAdapter
		viewModel.bind(this)
	}

	override fun render(uiState: LicensesUiState) {
		licensesAdapter.submitList(uiState.licenses)
	}
}