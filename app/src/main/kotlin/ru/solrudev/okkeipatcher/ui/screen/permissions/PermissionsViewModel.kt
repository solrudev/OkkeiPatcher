package ru.solrudev.okkeipatcher.ui.screen.permissions

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(permissionsFeature: PermissionsFeature) :
	FeatureViewModel<PermissionsEvent, PermissionsUiState>(permissionsFeature)