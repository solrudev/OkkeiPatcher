package ru.solrudev.okkeipatcher.ui.screen.permissions

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.screen.permissions.middleware.LoadRequiredPermissionsMiddleware
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsUiState
import ru.solrudev.okkeipatcher.ui.screen.permissions.reducer.PermissionsReducer
import javax.inject.Inject

@ViewModelScoped
class PermissionsFeature @Inject constructor(
	loadRequiredPermissionsMiddleware: LoadRequiredPermissionsMiddleware,
	permissionsReducer: PermissionsReducer
) : BaseFeature<PermissionsEvent, PermissionsUiState>(
	middlewares = listOf(loadRequiredPermissionsMiddleware),
	reducer = permissionsReducer,
	initialUiState = PermissionsUiState()
)