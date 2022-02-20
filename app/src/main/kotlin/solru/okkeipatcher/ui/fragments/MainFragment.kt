package solru.okkeipatcher.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import solru.okkeipatcher.R
import solru.okkeipatcher.databinding.FragmentMainBinding
import solru.okkeipatcher.viewmodels.MainViewModel

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

	private val viewModel: MainViewModel by activityViewModels()
	private val binding by viewBinding(FragmentMainBinding::bind)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		setupNavigation()
		viewLifecycleOwner.lifecycle.addObserver(viewModel)
		viewLifecycleOwner.lifecycleScope.observeViewModel()
	}

	private fun setupNavigation() {
		binding.buttonMainPatch.setOnClickListener {
			val toPatchFragment = MainFragmentDirections.actionMainFragmentToPatchFragment()
			findNavController().navigate(toPatchFragment)
		}
		binding.buttonMainRestore.setOnClickListener {
			val toRestoreFragment = MainFragmentDirections.actionMainFragmentToRestoreFragment()
			findNavController().navigate(toRestoreFragment)
		}
	}

	private fun CoroutineScope.observeViewModel() = launch {
		viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			observeIsPatchEnabled()
			observeIsRestoreEnabled()
		}
	}

	private fun CoroutineScope.observeIsPatchEnabled() = launch {
		viewModel.isPatchEnabled.collect {
			binding.buttonMainPatch.isEnabled = it
		}
	}

	private fun CoroutineScope.observeIsRestoreEnabled() = launch {
		viewModel.isRestoreEnabled.collect {
			binding.buttonMainRestore.isEnabled = it
		}
	}
}