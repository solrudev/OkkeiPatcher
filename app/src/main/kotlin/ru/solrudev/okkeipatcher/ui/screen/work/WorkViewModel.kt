package ru.solrudev.okkeipatcher.ui.screen.work

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.solrudev.okkeipatcher.ui.core.FeatureViewModel
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(workFeature: WorkFeature) :
	FeatureViewModel<WorkEvent, WorkUiState>(workFeature)