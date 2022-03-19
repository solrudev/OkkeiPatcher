package solru.okkeipatcher.ui.fragments

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.viewmodels.HomeViewModel
import solru.okkeipatcher.viewmodels.RestoreViewModel

@AndroidEntryPoint
class RestoreFragment : WorkFragment<RestoreViewModel>() {

	override val viewModel: RestoreViewModel by viewModels()
	private val homeViewModel: HomeViewModel by activityViewModels()

	override fun onSuccess() {
		homeViewModel.setIsPatched(false)
	}
}