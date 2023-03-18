package ru.solrudev.okkeipatcher.ui.screen.permissions.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.usecase.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent
import ru.solrudev.okkeipatcher.ui.screen.permissions.model.PermissionsEvent.RequiredPermissionsLoaded
import javax.inject.Inject

class LoadRequiredPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : JetMiddleware<PermissionsEvent> {

	override fun MiddlewareScope<PermissionsEvent>.apply() {
		launch {
			val requiredPermissions = getRequiredPermissionsUseCase()
			send(RequiredPermissionsLoaded(requiredPermissions))
		}
	}
}