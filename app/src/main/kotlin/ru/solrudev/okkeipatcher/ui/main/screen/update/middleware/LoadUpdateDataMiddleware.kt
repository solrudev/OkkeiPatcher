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

package ru.solrudev.okkeipatcher.ui.main.screen.update.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.solrudev.okkeipatcher.app.usecase.GetIsUpdateAvailableFlowUseCase
import ru.solrudev.okkeipatcher.app.usecase.GetUpdateDataUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataLoadingStarted
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateDataRequested
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.UpdateAvailable
import javax.inject.Inject

class LoadUpdateDataMiddleware @Inject constructor(
	private val getUpdateDataUseCase: GetUpdateDataUseCase,
	private val getIsUpdateAvailableFlowUseCase: GetIsUpdateAvailableFlowUseCase,
) : JetMiddleware<UpdateEvent> {

	override fun MiddlewareScope<UpdateEvent>.apply() {
		getIsUpdateAvailableFlowUseCase()
			.filter { it }
			.onEach { send(UpdateDataRequested(refresh = false)) }
			.launchIn(this)
		onEvent<UpdateDataRequested> { event ->
			send(UpdateDataLoadingStarted)
			val updateData = getUpdateDataUseCase(refresh = event.refresh)
			send(UpdateDataLoaded(updateData.sizeInMb, updateData.changelog))
			if (updateData.isAvailable) {
				send(UpdateStatusChanged(UpdateAvailable))
			}
		}
	}
}