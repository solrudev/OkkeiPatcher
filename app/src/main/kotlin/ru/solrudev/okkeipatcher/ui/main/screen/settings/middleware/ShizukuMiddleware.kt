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
import ru.solrudev.okkeipatcher.app.usecase.GetIsShizukuEnabledFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.PersistIsShizukuEnabledUseCase
import ru.solrudev.okkeipatcher.app.usecase.ToggleShizukuUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuChanged
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionGranted
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionRequested
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuToggled
import javax.inject.Inject

class ShizukuMiddleware @Inject constructor(
	private val toggleShizukuUseCase: ToggleShizukuUseCase,
	private val persistIsShizukuEnabledUseCase: PersistIsShizukuEnabledUseCase,
	private val getIsShizukuEnabledFlowUseCase: GetIsShizukuEnabledFlowUseCase
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		getIsShizukuEnabledFlowUseCase()
			.map { isShizukuEnabled -> ShizukuChanged(isShizukuEnabled) }
			.onEach(::send)
			.launchIn(this)
		onEvent<ShizukuToggled> {
			if (!toggleShizukuUseCase()) {
				send(ShizukuPermissionRequested)
			}
		}
		onEvent<ShizukuPermissionGranted> {
			persistIsShizukuEnabledUseCase(true)
		}
	}
}