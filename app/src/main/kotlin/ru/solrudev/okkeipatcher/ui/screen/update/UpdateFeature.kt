package ru.solrudev.okkeipatcher.ui.screen.update

import ru.solrudev.okkeipatcher.ui.core.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.update.middleware.LoadUpdateDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.screen.update.reducer.UpdateReducer
import javax.inject.Inject

class UpdateFeature @Inject constructor(
	loadUpdateDataMiddleware: LoadUpdateDataMiddleware,
	updateReducer: UpdateReducer
) : AssemblyFeature<UpdateEvent, UpdateUiState>(
	middlewares = listOf(loadUpdateDataMiddleware),
	reducer = updateReducer,
	initialUiState = UpdateUiState()
)