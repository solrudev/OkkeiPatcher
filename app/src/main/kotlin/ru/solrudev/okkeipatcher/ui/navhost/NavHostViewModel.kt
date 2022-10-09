package ru.solrudev.okkeipatcher.ui.navhost

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import javax.inject.Inject

@HiltViewModel
class NavHostViewModel @Inject constructor(navHostFeature: NavHostFeature) :
	FeatureViewModel<HostEvent, NavHostUiState>(navHostFeature)