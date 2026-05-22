/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.model.OperationMode
import ru.solrudev.okkeipatcher.app.usecase.CheckSaveDataAccessUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetAvailableOperationModesUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetOperationModeFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.OperationModeSelectionResult.RequestShizukuPermission
import ru.solrudev.okkeipatcher.app.usecase.PersistOperationModeUseCase
import ru.solrudev.okkeipatcher.app.usecase.SelectOperationModeUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.AvailableOperationModesLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.OperationModeChanged
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.OperationModeSelected
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionGranted
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionRequested
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuServiceNotRunningReported
import javax.inject.Inject
import ru.solrudev.okkeipatcher.app.usecase.OperationModeSelectionResult.ShizukuServiceNotRunning as ShizukuServiceNotRunning

class OperationModeMiddleware @Inject constructor(
	private val getAvailableOperationModesUseCase: GetAvailableOperationModesUseCase,
	private val selectOperationModeUseCase: SelectOperationModeUseCase,
	private val persistOperationModeUseCase: PersistOperationModeUseCase,
	private val checkSaveDataAccessUseCase: CheckSaveDataAccessUseCase,
	private val getOperationModeFlowUseCase: GetOperationModeFlowUseCase
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		launch {
			send(AvailableOperationModesLoaded(getAvailableOperationModesUseCase()))
		}
		getOperationModeFlowUseCase()
			.map(::OperationModeChanged)
			.onEach(::send)
			.launchIn(this)
		onEvent<OperationModeSelected> { event ->
			when (selectOperationModeUseCase(event.operationMode)) {
				RequestShizukuPermission -> send(ShizukuPermissionRequested)
				ShizukuServiceNotRunning -> send(ShizukuServiceNotRunningReported)
				else -> { // no-op
				}
			}
		}
		onEvent<ShizukuPermissionGranted> {
			persistOperationModeUseCase(OperationMode.Shizuku)
			checkSaveDataAccessUseCase()
		}
	}
}