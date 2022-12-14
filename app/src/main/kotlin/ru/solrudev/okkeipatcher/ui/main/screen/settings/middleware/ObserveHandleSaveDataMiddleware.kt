package ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware

import io.github.solrudev.jetmvi.Middleware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.solrudev.okkeipatcher.domain.usecase.app.GetHandleSaveDataFlowUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.HandleSaveDataChanged
import javax.inject.Inject

class ObserveHandleSaveDataMiddleware @Inject constructor(
	private val getHandleSaveDataFlowUseCase: GetHandleSaveDataFlowUseCase
) : Middleware<SettingsEvent> {

	override fun apply(events: Flow<SettingsEvent>) = getHandleSaveDataFlowUseCase().map(::HandleSaveDataChanged)
}