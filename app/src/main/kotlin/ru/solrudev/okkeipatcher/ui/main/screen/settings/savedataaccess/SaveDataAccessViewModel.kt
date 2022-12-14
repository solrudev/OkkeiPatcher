package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess

import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.solrudev.jetmvi.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessUiState
import javax.inject.Inject

@HiltViewModel
class SaveDataAccessViewModel @Inject constructor(saveDataAccessFeature: SaveDataAccessFeature) :
	FeatureViewModel<SaveDataAccessEvent, SaveDataAccessUiState>(saveDataAccessFeature)