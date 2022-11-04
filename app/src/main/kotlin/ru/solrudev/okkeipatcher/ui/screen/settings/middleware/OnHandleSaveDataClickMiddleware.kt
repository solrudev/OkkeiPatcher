package ru.solrudev.okkeipatcher.ui.screen.settings.middleware

import io.github.solrudev.jetmvi.Middleware
import io.github.solrudev.jetmvi.collectEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import ru.solrudev.okkeipatcher.domain.usecase.app.GetHandleSaveDataFlowUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.GetIsSaveDataAccessGrantedUseCase
import ru.solrudev.okkeipatcher.domain.usecase.app.PersistHandleSaveDataUseCase
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent.HandleSaveDataClicked
import ru.solrudev.okkeipatcher.ui.screen.settings.model.SettingsEvent.SaveDataAccessRequested
import javax.inject.Inject

class OnHandleSaveDataClickMiddleware @Inject constructor(
	private val getHandleSaveDataFlowUseCase: GetHandleSaveDataFlowUseCase,
	private val getIsSaveDataAccessGrantedUseCase: GetIsSaveDataAccessGrantedUseCase,
	private val persistHandleSaveDataUseCase: PersistHandleSaveDataUseCase
) : Middleware<SettingsEvent> {

	override fun apply(events: Flow<SettingsEvent>) = flow {
		events.collectEvent<HandleSaveDataClicked> {
			val handleSaveData = getHandleSaveDataFlowUseCase().first()
			val isSaveDataAccessGranted = getIsSaveDataAccessGrantedUseCase()
			when {
				handleSaveData -> persistHandleSaveDataUseCase(false)
				isSaveDataAccessGranted -> persistHandleSaveDataUseCase(true)
				!isSaveDataAccessGranted -> emit(SaveDataAccessRequested)
			}
		}
	}
}