package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.*
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.UpdateAvailable
import javax.inject.Inject

class LoadUpdateDataMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase
) : JetMiddleware<UpdateEvent> {

	override fun MiddlewareScope<UpdateEvent>.apply() {
		onEvent<UpdateDataRequested> { event ->
			send(UpdateDataLoadingStarted)
			val updateData = getUpdateDataUseCase(refresh = event.refresh)
			send(UpdateDataLoaded(updateData.sizeInMb, updateData.changelog))
			if (updateData.isAvailable) {
				send(UpdateStatusChanged(UpdateAvailable))
			}
		}
	}
}