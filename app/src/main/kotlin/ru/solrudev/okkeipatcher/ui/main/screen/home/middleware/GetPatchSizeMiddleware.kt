package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.*
import javax.inject.Inject

class GetPatchSizeMiddleware @Inject constructor(
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase
) : JetMiddleware<HomeEvent> {

	@Suppress("KotlinConstantConditions")
	override fun MiddlewareScope<HomeEvent>.apply() {
		var isLoading = false
		onEvent<PatchRequested> {
			if (!isLoading) {
				isLoading = true
				send(PatchSizeLoadingStarted)
				val patchSize = getPatchSizeInMbUseCase()
				send(PatchSizeLoaded(patchSize))
				isLoading = false
			}
		}
	}
}