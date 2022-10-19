package ru.solrudev.okkeipatcher.ui.screen.update.middleware

import kotlinx.coroutines.flow.*
import ru.solrudev.okkeipatcher.domain.usecase.app.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent.*
import javax.inject.Inject

class LoadUpdateDataMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase
) : Middleware<UpdateEvent> {

	override fun apply(events: Flow<UpdateEvent>) = flow {
		events
			.filterIsInstance<UpdateDataRequested>()
			.onEach { emit(UpdateDataLoadingStarted) }
			.map { event -> getUpdateDataUseCase(refresh = event.refresh) }
			.map(::UpdateDataLoaded)
			.collect(::emit)
	}
}