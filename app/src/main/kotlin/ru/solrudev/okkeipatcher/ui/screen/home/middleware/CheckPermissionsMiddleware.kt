package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PermissionsChecked
import javax.inject.Inject

class CheckPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<PermissionsCheckRequested> {
			val allPermissionsGranted = getRequiredPermissionsUseCase().all { (_, isGranted) -> isGranted }
			emit(PermissionsChecked(allPermissionsGranted))
		}
	}
}