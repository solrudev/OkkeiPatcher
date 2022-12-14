package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.*
import javax.inject.Inject

class GetPatchSizeMiddleware @Inject constructor(
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		var isLoading = false
		events
			.filterIsInstance<PatchRequested>()
			.filterNot { isLoading }
			.conflate()
			.collect {
				isLoading = true
				emit(PatchSizeLoadingStarted)
				val patchSize = getPatchSizeInMbUseCase()
				emit(PatchSizeLoaded(patchSize))
				isLoading = false
			}
	}
}