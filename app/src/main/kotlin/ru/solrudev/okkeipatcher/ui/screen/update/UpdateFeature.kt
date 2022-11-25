package ru.solrudev.okkeipatcher.ui.screen.update

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.screen.update.middleware.LoadUpdateDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.screen.update.model.UpdateUiState
import ru.solrudev.okkeipatcher.ui.screen.update.reducer.UpdateReducer
import javax.inject.Inject

@ViewModelScoped
class UpdateFeature @Inject constructor(
	loadUpdateDataMiddleware: LoadUpdateDataMiddleware,
	updateReducer: UpdateReducer
) : BaseFeature<UpdateEvent, UpdateUiState>(
	middlewares = listOf(loadUpdateDataMiddleware),
	reducer = updateReducer,
	initialUiState = UpdateUiState()
)