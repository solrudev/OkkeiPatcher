package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.ClearDataUseCase
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.onSuccess
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.*
import javax.inject.Inject

class ClearDataMiddleware @Inject constructor(
	private val clearDataUseCase: ClearDataUseCase
) : JetMiddleware<ClearDataEvent> {

	override fun MiddlewareScope<ClearDataEvent>.apply() {
		onEvent<ClearingRequested> {
			clearDataUseCase()
				.onFailure { send(ClearingFailed(it.reason)) }
				.onSuccess { send(DataCleared) }
		}
	}
}