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