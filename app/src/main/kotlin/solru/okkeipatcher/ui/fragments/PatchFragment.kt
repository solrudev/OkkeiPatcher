package solru.okkeipatcher.ui.fragments

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.viewmodels.HomeViewModel
import solru.okkeipatcher.viewmodels.PatchViewModel

@AndroidEntryPoint
class PatchFragment : WorkFragment<PatchViewModel>() {

	override val viewModel: PatchViewModel by viewModels()
	private val homeViewModel: HomeViewModel by activityViewModels()

	override fun onSuccess() {
		homeViewModel.setIsPatched(true)
	}
}