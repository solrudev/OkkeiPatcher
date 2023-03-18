package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsChecked
import javax.inject.Inject

class CheckPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : JetMiddleware<NavHostEvent> {

	override fun MiddlewareScope<NavHostEvent>.apply() {
		onEvent<PermissionsCheckRequested> {
			val allPermissionsGranted = getRequiredPermissionsUseCase().all { (_, isGranted) -> isGranted }
			send(PermissionsChecked(allPermissionsGranted))
		}
	}
}