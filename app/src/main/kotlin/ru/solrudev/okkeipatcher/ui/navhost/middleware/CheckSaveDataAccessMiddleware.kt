package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.CheckSaveDataAccessUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import javax.inject.Inject

class CheckSaveDataAccessMiddleware @Inject constructor(
	private val checkSaveDataAccessUseCase: CheckSaveDataAccessUseCase
) : Middleware<NavHostEvent> {

	override fun apply(events: Flow<NavHostEvent>) = flow<Nothing> {
		checkSaveDataAccessUseCase()
	}
}