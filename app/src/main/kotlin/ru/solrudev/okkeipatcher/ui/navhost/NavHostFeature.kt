package ru.solrudev.okkeipatcher.ui.navhost

import ru.solrudev.okkeipatcher.ui.core.Feature
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
	navHostReducer: NavHostReducer
) : Feature<NavHostEvent, NavHostUiState>(
	middlewares = listOf(
		checkPermissionsMiddleware,
		observePendingWorkMiddleware,
		checkSaveDataAccessMiddleware
	),
	reducer = navHostReducer,
	initialUiState = NavHostUiState()
)