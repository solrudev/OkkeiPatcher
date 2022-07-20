package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.onSuccess
import ru.solrudev.okkeipatcher.domain.usecase.app.ClearDataUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent.*
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