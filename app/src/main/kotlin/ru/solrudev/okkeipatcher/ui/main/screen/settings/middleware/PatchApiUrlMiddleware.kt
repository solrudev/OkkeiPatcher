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
import kotlinx.coroutines.launch
import ru.solrudev.okkeipatcher.app.usecase.GetPatchApiUrlUseCase
import ru.solrudev.okkeipatcher.app.usecase.PersistPatchApiUrlUseCase
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.PatchApiUrlChanged
import javax.inject.Inject

class PatchApiUrlMiddleware @Inject constructor(
	private val getPatchApiUrlUseCase: GetPatchApiUrlUseCase,
	private val persistPatchApiUrlUseCase: PersistPatchApiUrlUseCase,
) : JetMiddleware<SettingsEvent> {

	override fun MiddlewareScope<SettingsEvent>.apply() {
		launch {
			send(PatchApiUrlChanged(getPatchApiUrlUseCase()))
		}
		onEvent<PatchApiUrlChanged> { event ->
			val persistedUrl = persistPatchApiUrlUseCase(event.patchApiUrl)
			if (persistedUrl != event.patchApiUrl) {
				send(PatchApiUrlChanged(persistedUrl))
			}
		}
	}
}