package ru.solrudev.okkeipatcher.ui.host.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.host.model.HostEvent.PermissionsChecked
import javax.inject.Inject

class CheckPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : Middleware<HostEvent> {

	override fun apply(events: Flow<HostEvent>) = flow {
		events.collectEvent<PermissionsCheckRequested> {
			val allPermissionsGranted = getRequiredPermissionsUseCase().all { (_, isGranted) -> isGranted }
			emit(PermissionsChecked(allPermissionsGranted))
		}
	}
}