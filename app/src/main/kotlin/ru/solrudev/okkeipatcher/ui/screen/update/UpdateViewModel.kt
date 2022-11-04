package ru.solrudev.okkeipatcher.ui.screen.update

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateUiState
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(updateFeature: UpdateFeature) :
	FeatureViewModel<UpdateEvent, UpdateUiState>(updateFeature)