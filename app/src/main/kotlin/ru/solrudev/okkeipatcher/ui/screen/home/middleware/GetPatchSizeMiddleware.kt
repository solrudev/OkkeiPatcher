package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeUiState
import ru.solrudev.okkeipatcher.ui.screen.home.model.PatchEvent.*
import javax.inject.Inject

class GetPatchSizeMiddleware @Inject constructor(
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase
) : Middleware<HomeEvent, HomeUiState> {

	override fun apply(events: Flow<HomeEvent>, state: Flow<HomeUiState>) = flow {
		events.collectEvent<PatchRequested> {
			emit(PatchSizeLoadingStarted)
			val patchSize = getPatchSizeInMbUseCase()
			emit(PatchSizeLoaded(patchSize))
		}
	}
}