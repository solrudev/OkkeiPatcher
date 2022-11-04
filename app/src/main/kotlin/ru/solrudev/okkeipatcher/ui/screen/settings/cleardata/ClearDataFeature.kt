package ru.solrudev.okkeipatcher.ui.screen.settings.cleardata

import io.github.solrudev.jetmvi.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.middleware.ClearDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.model.ClearDataUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.cleardata.reducer.ClearDataReducer
import javax.inject.Inject

class ClearDataFeature @Inject constructor(
	clearDataMiddleware: ClearDataMiddleware,
	clearDataReducer: ClearDataReducer
) : AssemblyFeature<ClearDataEvent, ClearDataUiState>(
	middlewares = listOf(clearDataMiddleware),
	reducer = clearDataReducer,
	initialUiState = ClearDataUiState()
)