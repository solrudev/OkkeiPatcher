package ru.solrudev.okkeipatcher.ui.main.screen.home

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.home.middleware.*
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.main.screen.home.reducer.HomeReducer
import javax.inject.Inject

@ViewModelScoped
class HomeFeature @Inject constructor(
	observePatchStatusMiddleware: ObservePatchStatusMiddleware,
	observePatchVersionMiddleware: ObservePatchVersionMiddleware,
	checkPatchUpdatesMiddleware: CheckPatchUpdatesMiddleware,
	enqueuePatchWorkMiddleware: EnqueuePatchWorkMiddleware,
	enqueueRestoreWorkMiddleware: EnqueueRestoreWorkMiddleware,
	getPatchSizeMiddleware: GetPatchSizeMiddleware,
	homeReducer: HomeReducer
) : JetFeature<HomeEvent, HomeUiState>(
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