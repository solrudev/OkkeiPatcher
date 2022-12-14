package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.onSuccess
import ru.solrudev.okkeipatcher.domain.usecase.app.ClearDataUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent.*
import javax.inject.Inject

class ClearDataMiddleware @Inject constructor(
	private val clearDataUseCase: ClearDataUseCase
) : Middleware<ClearDataEvent> {

	override fun apply(events: Flow<ClearDataEvent>) = flow {
		events.collectEvent<ClearingRequested> {
			clearDataUseCase()
				.onFailure { emit(ClearingFailed(it.reason)) }
				.onSuccess { emit(DataCleared) }
		}
	}
}