package ru.solrudev.okkeipatcher.ui.screen.licenses.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetLicensesUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent.LicensesLoaded
import javax.inject.Inject

class LicensesMiddleware @Inject constructor(
	private val getLicensesUseCase: GetLicensesUseCase
) : Middleware<LicensesEvent> {

	override fun apply(events: Flow<LicensesEvent>) = flow {
		val licenses = getLicensesUseCase()
		emit(LicensesLoaded(licenses))
	}
}