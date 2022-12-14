package ru.solrudev.okkeipatcher.ui.main.screen.licenses

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesUiState
import javax.inject.Inject

@HiltViewModel
class LicensesViewModel @Inject constructor(licensesFeature: LicensesFeature) :
	FeatureViewModel<LicensesEvent, LicensesUiState>(licensesFeature)