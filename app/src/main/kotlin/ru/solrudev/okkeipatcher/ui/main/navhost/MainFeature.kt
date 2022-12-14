package ru.solrudev.okkeipatcher.ui.main.navhost

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.BaseFeature
import ru.solrudev.okkeipatcher.ui.main.navhost.middleware.CheckAndObserveUpdateMiddleware
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainUiState
import ru.solrudev.okkeipatcher.ui.main.navhost.reducer.MainReducer
import javax.inject.Inject

@ViewModelScoped
class MainFeature @Inject constructor(
	checkAndObserveUpdateMiddleware: CheckAndObserveUpdateMiddleware,
	mainReducer: MainReducer
) : BaseFeature<MainEvent, MainUiState>(
	middlewares = listOf(checkAndObserveUpdateMiddleware),
	reducer = mainReducer,
	initialUiState = MainUiState()
)