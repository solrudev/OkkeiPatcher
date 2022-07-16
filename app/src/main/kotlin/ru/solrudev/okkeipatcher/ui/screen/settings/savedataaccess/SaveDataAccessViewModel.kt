package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import javax.inject.Inject

@HiltViewModel
class SaveDataAccessViewModel @Inject constructor(saveDataAccessFeature: SaveDataAccessFeature) :
	FeatureViewModel<SaveDataAccessEvent, SaveDataAccessUiState>(saveDataAccessFeature)