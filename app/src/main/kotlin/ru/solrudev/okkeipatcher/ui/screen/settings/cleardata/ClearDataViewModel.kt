package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataUiState
import javax.inject.Inject

@HiltViewModel
class ClearDataViewModel @Inject constructor(clearDataFeature: ClearDataFeature) :
	FeatureViewModel<ClearDataEvent, ClearDataUiState>(clearDataFeature)