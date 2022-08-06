package ru.solrudev.okkeipatcher.ui.host

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent
import ru.solrudev.okkeipatcher.ui.host.model.HostUiState
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor(hostFeature: HostFeature) :
	FeatureViewModel<HostEvent, HostUiState>(hostFeature)