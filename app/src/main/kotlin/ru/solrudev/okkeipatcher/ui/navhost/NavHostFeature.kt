package ru.solrudev.okkeipatcher.ui.navhost

import ru.solrudev.okkeipatcher.ui.core.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckAndObserveUpdateMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.CheckSaveDataAccessMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.middleware.ObservePendingWorkMiddleware
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostUiState
import ru.solrudev.okkeipatcher.ui.navhost.reducer.NavHostReducer
import javax.inject.Inject

class NavHostFeature @Inject constructor(
	checkPermissionsMiddleware: CheckPermissionsMiddleware,
	observePendingWorkMiddleware: ObservePendingWorkMiddleware,
	checkSaveDataAccessMiddleware: CheckSaveDataAccessMiddleware,
	checkAndObserveUpdateMiddleware: CheckAndObserveUpdateMiddleware,
	navHostReducer: NavHostReducer
) : AssemblyFeature<NavHostEvent, NavHostUiState>(
	middlewares = listOf(
		checkPermissionsMiddleware,
		observePendingWorkMiddleware,
		checkSaveDataAccessMiddleware,
		checkAndObserveUpdateMiddleware
	),
	reducer = navHostReducer,
	initialUiState = NavHostUiState()
)