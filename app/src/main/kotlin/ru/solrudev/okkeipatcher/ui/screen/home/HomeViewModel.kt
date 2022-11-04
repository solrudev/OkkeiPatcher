package ru.solrudev.okkeipatcher.ui.screen.home

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(homeFeature: HomeFeature) :
	FeatureViewModel<HomeEvent, HomeUiState>(homeFeature)