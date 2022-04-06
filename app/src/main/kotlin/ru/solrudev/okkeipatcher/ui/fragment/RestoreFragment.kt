package ru.solrudev.okkeipatcher.ui.fragment

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.solrudev.okkeipatcher.ui.viewmodel.RestoreViewModel

@AndroidEntryPoint
class RestoreFragment : WorkFragment<RestoreViewModel>() {
	override val viewModel by viewModels<RestoreViewModel>()
}