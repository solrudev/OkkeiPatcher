/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.ui.main.screen.home.middleware

import io.github.solrudev.jetmvi.JetMiddleware
import io.github.solrudev.jetmvi.MiddlewareScope
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchSizeInMbUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.HomeEvent
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchRequested
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchSizeLoaded
import ru.solrudev.okkeipatcher.ui.main.screen.home.model.PatchEvent.PatchSizeLoadingStarted
import javax.inject.Inject

class GetPatchSizeMiddleware @Inject constructor(
	private val getPatchSizeInMbUseCase: GetPatchSizeInMbUseCase
) : JetMiddleware<HomeEvent> {

	@Suppress("KotlinConstantConditions")
	override fun MiddlewareScope<HomeEvent>.apply() {
		var isLoading = false
		onEvent<PatchRequested> {
			if (!isLoading) {
				isLoading = true
				send(PatchSizeLoadingStarted)
				val patchSize = getPatchSizeInMbUseCase()
				send(PatchSizeLoaded(patchSize))
				isLoading = false
			}
		}
	}
}