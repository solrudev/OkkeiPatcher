package ru.solrudev.okkeipatcher.ui.screen.licenses

import ru.solrudev.okkeipatcher.ui.core.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.licenses.middleware.LicensesMiddleware
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.screen.licenses.model.LicensesUiState
import ru.solrudev.okkeipatcher.ui.screen.licenses.reducer.LicensesReducer
import javax.inject.Inject

class LicensesFeature @Inject constructor(
	licensesMiddleware: LicensesMiddleware,
	licensesReducer: LicensesReducer
) : AssemblyFeature<LicensesEvent, LicensesUiState>(
	middlewares = listOf(licensesMiddleware),
	reducer = licensesReducer,
	initialUiState = LicensesUiState()
)