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

package ru.solrudev.okkeipatcher.ui.main.navhost.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import ru.solrudev.okkeipatcher.app.usecase.GetIsAppUpdatesCheckEnabledFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetIsUpdateAvailableFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent
import ru.solrudev.okkeipatcher.ui.main.navhost.model.MainEvent.UpdateAvailabilityChanged
import javax.inject.Inject

class CheckAndObserveUpdateMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase,
	private val getIsUpdateAvailableFlowUseCase: GetIsUpdateAvailableFlowUseCase,
	private val getIsAppUpdatesCheckEnabledFlowUseCase: GetIsAppUpdatesCheckEnabledFlowUseCase
) : JetMiddleware<MainEvent> {

	override fun MiddlewareScope<MainEvent>.apply() {
		getIsAppUpdatesCheckEnabledFlowUseCase()
			.filter { it }
			.take(1)
			.onEach { getUpdateDataUseCase(refresh = true) }
			.launchIn(this)
		getIsUpdateAvailableFlowUseCase()
			.map(::UpdateAvailabilityChanged)
			.onEach(::send)
			.launchIn(this)
	}
}