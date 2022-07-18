package ru.solrudev.okkeipatcher.ui.screen.home

import ru.solrudev.okkeipatcher.ui.core.Feature
import ru.solrudev.okkeipatcher.ui.screen.home.middleware.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.reducer.HomeReducer
import javax.inject.Inject

class HomeFeature @Inject constructor(
	checkRequiredPermissionsMiddleware: CheckRequiredPermissionsMiddleware,
	checkSaveDataAccessMiddleware: CheckSaveDataAccessMiddleware,
	observePatchStatusMiddleware: ObservePatchStatusMiddleware,
	checkPatchUpdatesMiddleware: CheckPatchUpdatesMiddleware,
	checkPendingWorksMiddleware: CheckPendingWorksMiddleware,
	enqueuePatchWorkMiddleware: EnqueuePatchWorkMiddleware,
	enqueueRestoreWorkMiddleware: EnqueueRestoreWorkMiddleware,
	getPatchSizeMiddleware: GetPatchSizeMiddleware,
	homeReducer: HomeReducer
) : Feature<HomeEvent, HomeUiState>(
	middlewares = listOf(
		checkRequiredPermissionsMiddleware,
		checkSaveDataAccessMiddleware,
		observePatchStatusMiddleware,
		checkPatchUpdatesMiddleware,
		checkPendingWorksMiddleware,
		enqueuePatchWorkMiddleware,
		enqueueRestoreWorkMiddleware,
		getPatchSizeMiddleware
	),
	reducer = homeReducer,
	initialUiState = HomeUiState()
)