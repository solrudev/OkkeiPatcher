package ru.solrudev.okkeipatcher.ui.screen.home.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.work.EnqueueRestoreWorkUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.screen.home.model.HomeEvent.WorkIsPending
import ru.solrudev.okkeipatcher.ui.screen.home.model.RestoreEvent.StartRestore
import javax.inject.Inject

class EnqueueRestoreWorkMiddleware @Inject constructor(
	private val enqueueRestoreWorkUseCase: EnqueueRestoreWorkUseCase
) : Middleware<HomeEvent> {

	override fun apply(events: Flow<HomeEvent>) = flow {
		events.collectEvent<StartRestore> {
			val restoreWork = enqueueRestoreWorkUseCase()
			emit(WorkIsPending(restoreWork))
		}
	}
}