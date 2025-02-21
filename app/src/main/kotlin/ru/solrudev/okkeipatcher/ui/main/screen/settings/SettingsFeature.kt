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

package ru.solrudev.okkeipatcher.ui.main.screen.settings

import dagger.hilt.android.scopes.ViewModelScoped
import io.github.solrudev.jetmvi.JetFeature
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ObserveHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ObserveThemeMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.PersistThemeMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.ToggleHandleSaveDataMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.middleware.UpdateChecksMiddleware
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.main.screen.settings.reducer.SettingsReducer
import javax.inject.Inject

@ViewModelScoped
class SettingsFeature @Inject constructor(
	observeHandleSaveDataMiddleware: ObserveHandleSaveDataMiddleware,
	toggleHandleSaveDataMiddleware: ToggleHandleSaveDataMiddleware,
	observeThemeMiddleware: ObserveThemeMiddleware,
	persistThemeMiddleware: PersistThemeMiddleware,
	updateChecksMiddleware: UpdateChecksMiddleware,
	settingsReducer: SettingsReducer
) : JetFeature<SettingsEvent, SettingsUiState>(
	middlewares = listOf(
		observeHandleSaveDataMiddleware,
		toggleHandleSaveDataMiddleware,
		observeThemeMiddleware,
		persistThemeMiddleware,
		updateChecksMiddleware
	),
	reducer = settingsReducer,
	initialUiState = SettingsUiState()
)