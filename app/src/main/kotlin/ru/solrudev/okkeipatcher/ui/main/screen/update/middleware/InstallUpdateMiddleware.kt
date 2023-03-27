package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.InstallUpdateUseCase
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.onSuccess
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateInstallRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.*
import javax.inject.Inject

class InstallUpdateMiddleware @Inject constructor(
	private val installUpdateUseCase: InstallUpdateUseCase
) : JetMiddleware<UpdateEvent> {

	override fun MiddlewareScope<UpdateEvent>.apply() {
		onEvent<UpdateInstallRequested> {
			send(UpdateStatusChanged(Installing))
			installUpdateUseCase()
				.onSuccess { send(UpdateStatusChanged(NoUpdate)) }
				.onFailure { failure -> send(UpdateStatusChanged(Failed(failure.reason))) }
		}
	}
}