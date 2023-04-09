/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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