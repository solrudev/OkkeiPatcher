package ru.solrudev.okkeipatcher.ui.screen.permissions

import io.github.solrudev.jetmvi.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.permissions.middleware.LoadRequiredPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.reducer.PermissionsReducer
import javax.inject.Inject

class PermissionsFeature @Inject constructor(
	loadRequiredPermissionsMiddleware: LoadRequiredPermissionsMiddleware,
	permissionsReducer: PermissionsReducer
) : AssemblyFeature<PermissionsEvent, PermissionsUiState>(
	middlewares = listOf(loadRequiredPermissionsMiddleware),
	reducer = permissionsReducer,
	initialUiState = PermissionsUiState()
)