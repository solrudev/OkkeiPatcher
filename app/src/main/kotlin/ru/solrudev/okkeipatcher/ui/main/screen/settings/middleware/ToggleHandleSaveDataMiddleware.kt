package ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.app.usecase.GetHandleSaveDataFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetIsSaveDataAccessGrantedUseCase
import ru.solrudev.okkeipatcher.app.usecase.PersistHandleSaveDataUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.HandleSaveDataToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.SaveDataAccessRequested
import javax.inject.Inject

class ToggleHandleSaveDataMiddleware @Inject constructor(
	private val getHandleSaveDataFlowUseCase: GetHandleSaveDataFlowUseCase,
	private val getIsSaveDataAccessGrantedUseCase: GetIsSaveDataAccessGrantedUseCase,
	private val persistHandleSaveDataUseCase: PersistHandleSaveDataUseCase
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		onEvent<HandleSaveDataToggled> {
			val handleSaveData = getHandleSaveDataFlowUseCase().first()
			val isSaveDataAccessGranted = getIsSaveDataAccessGrantedUseCase()
			when {
				handleSaveData -> persistHandleSaveDataUseCase(false)
				isSaveDataAccessGranted -> persistHandleSaveDataUseCase(true)
				else -> send(SaveDataAccessRequested)
			}
		}
	}
}