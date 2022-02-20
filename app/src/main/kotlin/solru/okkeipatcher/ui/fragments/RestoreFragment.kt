package solru.okkeipatcher.ui.fragments

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.viewmodels.MainViewModel
import solru.okkeipatcher.viewmodels.RestoreViewModel

@AndroidEntryPoint
class RestoreFragment : WorkFragment() {

	override val viewModel: RestoreViewModel by viewModels()
	private val mainViewModel: MainViewModel by activityViewModels()

	override fun onSuccess() {
		mainViewModel.setIsPatched(false)
	}
}