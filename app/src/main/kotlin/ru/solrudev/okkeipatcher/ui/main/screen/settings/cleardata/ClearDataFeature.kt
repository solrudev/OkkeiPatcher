package ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.middleware.ClearDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.cleardata.reducer.ClearDataReducer
import javax.inject.Inject

@ViewModelScoped
class ClearDataFeature @Inject constructor(
	clearDataMiddleware: ClearDataMiddleware,
	clearDataReducer: ClearDataReducer
) : BaseFeature<ClearDataEvent, ClearDataUiState>(
	middlewares = listOf(clearDataMiddleware),
	reducer = clearDataReducer,
	initialUiState = ClearDataUiState()
)