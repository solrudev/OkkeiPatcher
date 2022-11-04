package ru.solrudev.okkeipatcher.ui.screen.home

import ru.solrudev.okkeipatcher.ui.core.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.home.middleware.*
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.reducer.HomeReducer
import javax.inject.Inject

class HomeFeature @Inject constructor(
	observePatchStatusMiddleware: ObservePatchStatusMiddleware,
	observePatchVersionMiddleware: ObservePatchVersionMiddleware,
	checkPatchUpdatesMiddleware: CheckPatchUpdatesMiddleware,
	enqueuePatchWorkMiddleware: EnqueuePatchWorkMiddleware,
	enqueueRestoreWorkMiddleware: EnqueueRestoreWorkMiddleware,
	getPatchSizeMiddleware: GetPatchSizeMiddleware,
	homeReducer: HomeReducer
) : AssemblyFeature<HomeEvent, HomeUiState>(
	middlewares = listOf(
		observePatchStatusMiddleware,
		observePatchVersionMiddleware,
		checkPatchUpdatesMiddleware,
		enqueuePatchWorkMiddleware,
		enqueueRestoreWorkMiddleware,
		getPatchSizeMiddleware
	),
	reducer = homeReducer,
	initialUiState = HomeUiState()
)