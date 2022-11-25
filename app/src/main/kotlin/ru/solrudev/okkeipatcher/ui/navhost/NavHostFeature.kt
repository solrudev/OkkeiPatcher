package ru.solrudev.okkeipatcher.ui.navhost

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckAndObserveUpdateMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckSaveDataAccessMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.ObservePendingWorkMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.navhost.reducer.NavHostReducer
import javax.inject.Inject

@ViewModelScoped
class NavHostFeature @Inject constructor(
	checkPermissionsMiddleware: CheckPermissionsMiddleware,
	observePendingWorkMiddleware: ObservePendingWorkMiddleware,
	checkSaveDataAccessMiddleware: CheckSaveDataAccessMiddleware,
	checkAndObserveUpdateMiddleware: CheckAndObserveUpdateMiddleware,
	navHostReducer: NavHostReducer
) : BaseFeature<NavHostEvent, NavHostUiState>(
	middlewares = listOf(
		checkPermissionsMiddleware,
		observePendingWorkMiddleware,
		checkSaveDataAccessMiddleware,
		checkAndObserveUpdateMiddleware
	),
	reducer = navHostReducer,
	initialUiState = NavHostUiState()
)