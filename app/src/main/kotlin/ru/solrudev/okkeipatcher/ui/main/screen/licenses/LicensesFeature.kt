package ru.solrudev.okkeipatcher.ui.main.screen.licenses

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.middleware.LicensesMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesEvent
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.model.LicensesUiState
import ru.solrudev.okkeipatcher.ui.main.screen.licenses.reducer.LicensesReducer
import javax.inject.Inject

@ViewModelScoped
class LicensesFeature @Inject constructor(
	licensesMiddleware: LicensesMiddleware,
	licensesReducer: LicensesReducer
) : BaseFeature<LicensesEvent, LicensesUiState>(
	middlewares = listOf(licensesMiddleware),
	reducer = licensesReducer,
	initialUiState = LicensesUiState()
)