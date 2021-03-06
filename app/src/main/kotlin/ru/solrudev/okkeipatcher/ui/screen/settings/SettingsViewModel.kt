package ru.solrudev.okkeipatcher.ui.screen.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsUiState
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(settingsFeature: SettingsFeature) :
	FeatureViewModel<SettingsEvent, SettingsUiState>(settingsFeature)