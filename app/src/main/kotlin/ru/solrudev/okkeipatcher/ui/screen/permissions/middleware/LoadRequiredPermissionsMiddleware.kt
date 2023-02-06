package ru.solrudev.okkeipatcher.ui.screen.permissions.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.app.usecase.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.RequiredPermissionsLoaded
import javax.inject.Inject

class LoadRequiredPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : Middleware<PermissionsEvent> {

	override fun apply(events: Flow<PermissionsEvent>) = flow {
		val requiredPermissions = getRequiredPermissionsUseCase()
		emit(RequiredPermissionsLoaded(requiredPermissions))
	}
}