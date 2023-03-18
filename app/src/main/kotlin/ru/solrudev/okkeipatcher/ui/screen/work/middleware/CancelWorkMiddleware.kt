package ru.solrudev.okkeipatcher.ui.screen.work.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.work.CancelWorkUseCase
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent
import ru.solrudev.okkeipatcher.ui.screen.work.model.WorkEvent.CancelWork
import javax.inject.Inject

class CancelWorkMiddleware @Inject constructor(
	private val cancelWorkUseCase: CancelWorkUseCase
) : JetMiddleware<WorkEvent> {

	override fun MiddlewareScope<WorkEvent>.apply() {
		onEvent<CancelWork> { event ->
			cancelWorkUseCase(event.work)
		}
	}
}