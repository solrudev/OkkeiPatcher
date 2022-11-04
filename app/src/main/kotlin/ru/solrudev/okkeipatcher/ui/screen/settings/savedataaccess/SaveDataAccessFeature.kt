package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess

import io.github.solrudev.jetmvi.AssemblyFeature
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.middleware.PersistHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.reducer.SaveDataAccessReducer
import javax.inject.Inject

class SaveDataAccessFeature @Inject constructor(
	persistHandleSaveDataMiddleware: PersistHandleSaveDataMiddleware,
	saveDataAccessReducer: SaveDataAccessReducer
) : AssemblyFeature<SaveDataAccessEvent, SaveDataAccessUiState>(
	middlewares = listOf(persistHandleSaveDataMiddleware),
	reducer = saveDataAccessReducer,
	initialUiState = SaveDataAccessUiState()
)