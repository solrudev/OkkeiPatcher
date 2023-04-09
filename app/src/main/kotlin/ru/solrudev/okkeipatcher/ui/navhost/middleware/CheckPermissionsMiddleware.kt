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

package ru.solrudev.okkeipatcher.ui.navhost.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.GetRequiredPermissionsUseCase
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsCheckRequested
import ru.solrudev.okkeipatcher.ui.navhost.model.NavHostEvent.PermissionsChecked
import javax.inject.Inject

class CheckPermissionsMiddleware @Inject constructor(
	private val getRequiredPermissionsUseCase: GetRequiredPermissionsUseCase
) : JetMiddleware<NavHostEvent> {

	override fun MiddlewareScope<NavHostEvent>.apply() {
		onEvent<PermissionsCheckRequested> {
			val allPermissionsGranted = getRequiredPermissionsUseCase().all { (_, isGranted) -> isGranted }
			send(PermissionsChecked(allPermissionsGranted))
		}
	}
}