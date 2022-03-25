package solru.okkeipatcher.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.databinding.FragmentHomeBinding
import solru.okkeipatcher.ui.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

	private val viewModel: HomeViewModel by viewModels()
	private val binding by viewBinding(FragmentHomeBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		viewLifecycleOwner.lifecycle.addObserver(viewModel)
		viewLifecycleOwner.lifecycleScope.observeUiState()
	}

	private fun setupNavigation() {
		binding.buttonMainPatch.setOnClickListener {
			val toPatchFragment = HomeFragmentDirections.actionHomeFragmentToPatchFragment()
			findNavController().navigate(toPatchFragment)
		}
		binding.buttonMainRestore.setOnClickListener {
			val toRestoreFragment = HomeFragmentDirections.actionHomeFragmentToRestoreFragment()
			findNavController().navigate(toRestoreFragment)
		}
	}

	private fun CoroutineScope.observeUiState() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			viewModel.uiState.collect { uiState ->
				binding.buttonMainPatch.isEnabled = uiState.isPatchEnabled
				binding.buttonMainRestore.isEnabled = uiState.isRestoreEnabled
				if (!uiState.checkedForPatchUpdates) {
					viewModel.checkPatchUpdates()
				}
				if (uiState.patchUpdatesAvailable) {
					view?.let {
						Snackbar.make(it, R.string.prompt_update_patch_available, Snackbar.LENGTH_LONG).show()
					}
					viewModel.patchUpdatesMessageShown()
				}
			}
		}
	}
}