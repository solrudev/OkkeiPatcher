/*
 * Okkei Patcher
 * Copyright (C) 2025 Ilya Fomichev
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
import ru.solrudev.okkeipatcher.app.usecase.GetIsAppUpdatesCheckEnabledFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetIsPatchUpdatesCheckEnabledFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.ToggleIsAppUpdatesCheckEnabledUseCase
import ru.solrudev.okkeipatcher.app.usecase.ToggleIsPatchUpdatesCheckEnabledUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsAppUpdatesCheckEnabledChanged
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsAppUpdatesCheckEnabledToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsPatchUpdatesCheckEnabledChanged
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsPatchUpdatesCheckEnabledToggled
import javax.inject.Inject

class UpdateChecksMiddleware @Inject constructor(
	private val getIsAppUpdatesCheckEnabledFlowUseCase: GetIsAppUpdatesCheckEnabledFlowUseCase,
	private val getIsPatchUpdatesCheckEnabledFlowUseCase: GetIsPatchUpdatesCheckEnabledFlowUseCase,
	private val toggleIsAppUpdatesCheckEnabledUseCase: ToggleIsAppUpdatesCheckEnabledUseCase,
	private val toggleIsPatchUpdatesCheckEnabledUseCase: ToggleIsPatchUpdatesCheckEnabledUseCase
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		getIsAppUpdatesCheckEnabledFlowUseCase()
			.map(::IsAppUpdatesCheckEnabledChanged)
			.onEach(::send)
			.launchIn(this)
		getIsPatchUpdatesCheckEnabledFlowUseCase()
			.map(::IsPatchUpdatesCheckEnabledChanged)
			.onEach(::send)
			.launchIn(this)
		onEvent<IsAppUpdatesCheckEnabledToggled> {
			toggleIsAppUpdatesCheckEnabledUseCase()
		}
		onEvent<IsPatchUpdatesCheckEnabledToggled> {
			toggleIsPatchUpdatesCheckEnabledUseCase()
		}
	}
}