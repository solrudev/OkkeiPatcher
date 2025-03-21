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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.model

import io.github.solrudev.jetmvi.JetEffect
import io.github.solrudev.jetmvi.JetEvent
import ru.solrudev.okkeipatcher.app.model.Theme

sealed interface SettingsEvent : JetEvent {
	data object HandleSaveDataToggled : SettingsEvent, SettingsEffect
	data object SaveDataAccessRequested : SettingsEvent
	data object SaveDataAccessRequestHandled : SettingsEvent
	data object IsAppUpdatesCheckEnabledToggled : SettingsEvent, SettingsEffect
	data object IsPatchUpdatesCheckEnabledToggled : SettingsEvent, SettingsEffect
	data class HandleSaveDataChanged(val handleSaveData: Boolean) : SettingsEvent
	data class IsAppUpdatesCheckEnabledChanged(val isAppUpdatesCheckEnabled: Boolean) : SettingsEvent
	data class IsPatchUpdatesCheckEnabledChanged(val isPatchUpdatesCheckEnabled: Boolean) : SettingsEvent
	data class ThemeChanged(val theme: Theme) : SettingsEvent
	data class PersistTheme(val theme: Theme) : SettingsEvent, SettingsEffect
}

sealed interface SettingsEffect : JetEffect