package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.yield
import ru.solrudev.okkeipatcher.domain.usecase.app.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import javax.inject.Inject

class ObservePatchStatusMiddleware @Inject constructor(
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = getPatchStatusFlowUseCase().map {
		yield()
		PatchStatusChanged(it)
	}
}