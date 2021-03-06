package ru.solrudev.okkeipatcher.ui.screen.permissions

import ru.solrudev.okkeipatcher.ui.core.Feature
import ru.solrudev.okkeipatcher.ui.screen.permissions.middleware.LoadRequiredPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.reducer.PermissionsReducer
import javax.inject.Inject

class PermissionsFeature @Inject constructor(
	loadRequiredPermissionsMiddleware: LoadRequiredPermissionsMiddleware,
	permissionsReducer: PermissionsReducer
) : Feature<PermissionsEvent, PermissionsUiState>(
	middlewares = listOf(loadRequiredPermissionsMiddleware),
	reducer = permissionsReducer,
	initialUiState = PermissionsUiState()
)