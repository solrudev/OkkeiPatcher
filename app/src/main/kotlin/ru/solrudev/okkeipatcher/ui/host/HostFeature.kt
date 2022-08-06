package ru.solrudev.okkeipatcher.ui.host

import ru.solrudev.okkeipatcher.ui.core.Feature
import ru.solrudev.okkeipatcher.ui.host.middleware.CheckPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.host.middleware.CheckSaveDataAccessMiddleware
import ru.solrudev.okkeipatcher.ui.host.middleware.ObservePendingWorkMiddleware
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent
import ru.solrudev.okkeipatcher.ui.host.model.HostUiState
import ru.solrudev.okkeipatcher.ui.host.reducer.HostReducer
import javax.inject.Inject

class HostFeature @Inject constructor(
	checkPermissionsMiddleware: CheckPermissionsMiddleware,
	observePendingWorkMiddleware: ObservePendingWorkMiddleware,
	checkSaveDataAccessMiddleware: CheckSaveDataAccessMiddleware,
	hostReducer: HostReducer
) : Feature<HostEvent, HostUiState>(
	middlewares = listOf(
		checkPermissionsMiddleware,
		observePendingWorkMiddleware,
		checkSaveDataAccessMiddleware
	),
	reducer = hostReducer,
	initialUiState = HostUiState()
)