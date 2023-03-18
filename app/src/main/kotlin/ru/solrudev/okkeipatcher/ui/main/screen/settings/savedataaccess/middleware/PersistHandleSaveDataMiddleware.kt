package ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.PersistHandleSaveDataUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.HandleSaveDataEnabled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.savedataaccess.model.SaveDataAccessEvent.PermissionGranted
import javax.inject.Inject

class PersistHandleSaveDataMiddleware @Inject constructor(
	private val persistHandleSaveDataUseCase: PersistHandleSaveDataUseCase
) : JetMiddleware<SaveDataAccessEvent> {

	override fun MiddlewareScope<SaveDataAccessEvent>.apply() {
		onEvent<PermissionGranted> {
			persistHandleSaveDataUseCase(true)
			send(HandleSaveDataEnabled)
		}
	}
}