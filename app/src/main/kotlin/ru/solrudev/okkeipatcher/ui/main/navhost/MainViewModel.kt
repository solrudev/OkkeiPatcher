package ru.solrudev.okkeipatcher.ui.main.navhost

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(mainFeature: MainFeature) :
	FeatureViewModel<MainEvent, MainUiState>(mainFeature)