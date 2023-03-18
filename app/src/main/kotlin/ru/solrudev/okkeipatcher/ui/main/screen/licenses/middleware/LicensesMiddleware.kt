package ru.solrudev.okkeipatcher.ui.main.screen.licenses.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.usecase.GetLicensesUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent.LicensesLoaded
import javax.inject.Inject

class LicensesMiddleware @Inject constructor(
	private val getLicensesUseCase: GetLicensesUseCase
) : JetMiddleware<LicensesEvent> {

	override fun MiddlewareScope<LicensesEvent>.apply() {
		launch {
			val licenses = getLicensesUseCase()
			send(LicensesLoaded(licenses))
		}
	}
}