package ru.solrudev.okkeipatcher.ui.screen.work

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.screen.work.middleware.CancelWorkMiddleware
import ru.solrudev.okkeipatcher.ui.screen.work.middleware.ObserveWorkMiddleware
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkUiState
import ru.solrudev.okkeipatcher.ui.screen.work.reducer.WorkReducer
import javax.inject.Inject

@ViewModelScoped
class WorkFeature @Inject constructor(
	observeWorkMiddleware: ObserveWorkMiddleware,
	cancelWorkMiddleware: CancelWorkMiddleware,
	workReducer: WorkReducer
) : BaseFeature<WorkEvent, WorkUiState>(
	middlewares = listOf(observeWorkMiddleware, cancelWorkMiddleware),
	reducer = workReducer,
	initialUiState = WorkUiState()
)