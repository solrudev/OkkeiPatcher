package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.yield
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchVersionFlowUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchVersionChanged
import javax.inject.Inject

class ObservePatchVersionMiddleware @Inject constructor(
	private val getPatchVersionFlowUseCase: GetPatchVersionFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = getPatchVersionFlowUseCase().map {
		yield()
		PatchVersionChanged(it)
	}
}