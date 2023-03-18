package ru.solrudev.okkeipatcher.ui.main.screen.update

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.update.middleware.LoadUpdateDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.main.screen.update.reducer.UpdateReducer
import javax.inject.Inject

@ViewModelScoped
class UpdateFeature @Inject constructor(
	loadUpdateDataMiddleware: LoadUpdateDataMiddleware,
	updateReducer: UpdateReducer
) : JetFeature<UpdateEvent, UpdateUiState>(
	middlewares = listOf(loadUpdateDataMiddleware),
	reducer = updateReducer,
	initialUiState = UpdateUiState()
)