package ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.middleware

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.PersistHandleSaveDataUseCase
import ru.solrudev.okkeipatcher.ui.core.Middleware
import ru.solrudev.okkeipatcher.ui.core.collectEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent.HandleSaveDataEnabled
import ru.solrudev.okkeipatcher.ui.screen.settings.savedataaccess.model.SaveDataAccessEvent.PermissionGranted
import javax.inject.Inject

class PersistHandleSaveDataMiddleware @Inject constructor(
	private val persistHandleSaveDataUseCase: PersistHandleSaveDataUseCase
) : Middleware<SaveDataAccessEvent> {

	override fun apply(events: Flow<SaveDataAccessEvent>) = flow {
		events.collectEvent<PermissionGranted> {
			persistHandleSaveDataUseCase(true)
			emit(HandleSaveDataEnabled)
		}
	}
}