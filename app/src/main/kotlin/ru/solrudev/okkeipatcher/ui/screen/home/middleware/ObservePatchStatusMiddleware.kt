package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.yield
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchStatusFlowUseCase
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.PatchStatusChanged
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchStatus.NotPatched
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchStatus.Patched
import javax.inject.Inject

class ObservePatchStatusMiddleware @Inject constructor(
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = getPatchStatusFlowUseCase().map { isPatched ->
		yield()
		val status = if (isPatched) Patched else NotPatched
		PatchStatusChanged(status)
	}
}