package ru.solrudev.okkeipatcher.ui.screen.licenses

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesUiState
import javax.inject.Inject

@HiltViewModel
class LicensesViewModel @Inject constructor(licensesFeature: LicensesFeature) :
	FeatureViewModel<LicensesEvent, LicensesUiState>(licensesFeature)