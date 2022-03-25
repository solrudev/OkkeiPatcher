package solru.okkeipatcher.ui.fragment

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.ui.viewmodel.RestoreViewModel

@AndroidEntryPoint
class RestoreFragment : WorkFragment<RestoreViewModel>() {
	override val viewModel: RestoreViewModel by viewModels()
}