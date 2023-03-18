package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.middleware.PersistHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.reducer.SaveDataAccessReducer
import javax.inject.Inject

@ViewModelScoped
class SaveDataAccessFeature @Inject constructor(
	persistHandleSaveDataMiddleware: PersistHandleSaveDataMiddleware,
	saveDataAccessReducer: SaveDataAccessReducer
) : JetFeature<SaveDataAccessEvent, SaveDataAccessUiState>(
	middlewares = listOf(persistHandleSaveDataMiddleware),
	reducer = saveDataAccessReducer,
	initialUiState = SaveDataAccessUiState()
)