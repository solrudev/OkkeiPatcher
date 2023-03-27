package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.work.EnqueueUpdateDownloadWorkUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDownloadRequested
import javax.inject.Inject

class EnqueueUpdateMiddleware @Inject constructor(
	private val enqueueUpdateDownloadWorkUseCase: EnqueueUpdateDownloadWorkUseCase
) : JetMiddleware<UpdateEvent> {

	override fun MiddlewareScope<UpdateEvent>.apply() {
		onEvent<UpdateDownloadRequested> {
			enqueueUpdateDownloadWorkUseCase()
		}
	}
}