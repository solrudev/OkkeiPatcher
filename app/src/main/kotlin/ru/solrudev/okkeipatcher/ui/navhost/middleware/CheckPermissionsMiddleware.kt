package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsChecked
import javax.inject.Inject

class CheckPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : Middleware<NavHostEvent> {

	override fun apply(events: Flow<NavHostEvent>) = flow {
		events.collectEvent<PermissionsCheckRequested> {
			val allPermissionsGranted = getRequiredPermissionsUseCase().all { (_, isGranted) -> isGranted }
			emit(PermissionsChecked(allPermissionsGranted))
		}
	}
}