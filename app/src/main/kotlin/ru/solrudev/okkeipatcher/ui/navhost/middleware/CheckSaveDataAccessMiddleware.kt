package ru.solrudev.okkeipatcher.ui.navhost.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.CheckSaveDataAccessUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.navhost.model.HostEvent
import javax.inject.Inject

class CheckSaveDataAccessMiddleware @Inject constructor(
	private val checkSaveDataAccessUseCase: CheckSaveDataAccessUseCase
) : Middleware<HostEvent> {

	override fun apply(events: Flow<HostEvent>) = flow<Nothing> {
		checkSaveDataAccessUseCase()
	}
}