package ru.solrudev.okkeipatcher.ui.screen.home

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(homeFeature: HomeFeature) :
	FeatureViewModel<HomeEvent, HomeUiState>(homeFeature)