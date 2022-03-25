package solru.okkeipatcher.ui.fragment

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import solru.okkeipatcher.ui.viewmodel.PatchViewModel

@AndroidEntryPoint
class PatchFragment : WorkFragment<PatchViewModel>() {
	override val viewModel: PatchViewModel by viewModels()
}