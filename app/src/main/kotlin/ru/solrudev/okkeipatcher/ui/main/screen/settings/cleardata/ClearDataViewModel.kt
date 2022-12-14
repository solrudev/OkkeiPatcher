package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataUiState
import javax.inject.Inject

@HiltViewModel
class ClearDataViewModel @Inject constructor(clearDataFeature: ClearDataFeature) :
	FeatureViewModel<ClearDataEvent, ClearDataUiState>(clearDataFeature)