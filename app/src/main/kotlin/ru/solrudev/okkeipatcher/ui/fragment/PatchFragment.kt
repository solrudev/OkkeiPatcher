package ru.solrudev.okkeipatcher.ui.fragment

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.ui.viewmodel.PatchViewModel

@AndroidEntryPoint
class PatchFragment : WorkFragment<PatchViewModel>() {
	override val viewModel by viewModels<PatchViewModel>()
}