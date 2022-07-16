package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess

import ru.solrudev.okkeipatcher.ui.core.Feature
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.middleware.PersistHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessUiState
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.reducer.SaveDataAccessReducer
import javax.inject.Inject

class SaveDataAccessFeature @Inject constructor(
	persistHandleSaveDataMiddleware: PersistHandleSaveDataMiddleware,
	saveDataAccessReducer: SaveDataAccessReducer
) : Feature<SaveDataAccessEvent, SaveDataAccessUiState>(
	middlewares = listOf(persistHandleSaveDataMiddleware),
	reducer = saveDataAccessReducer,
	initialUiState = SaveDataAccessUiState()
)