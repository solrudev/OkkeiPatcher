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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.controller

import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.preference.SwitchPreferenceCompat
import io.github.solrudev.jetmvi.JetView
import ru.solrudev.okkeipatcher.ui.main.screen.settings.HapticFeedbackCallback
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsAppUpdatesCheckEnabledToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.IsPatchUpdatesCheckEnabledToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState

class UpdatesSettingsController(
	private val isAppUpdatesCheckEnabled: SwitchPreferenceCompat?,
	private val isPatchUpdatesCheckEnabled: SwitchPreferenceCompat?,
	private val viewModel: SettingsViewModel,
	private val hapticFeedbackCallback: HapticFeedbackCallback
) : JetView<SettingsUiState> {

	init {
		isAppUpdatesCheckEnabled?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(IsAppUpdatesCheckEnabledToggled)
			hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
			true
		}
		isPatchUpdatesCheckEnabled?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(IsPatchUpdatesCheckEnabledToggled)
			hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
			true
		}
	}

	override val trackedState = listOf(
		SettingsUiState::isAppUpdatesCheckEnabled,
		SettingsUiState::isPatchUpdatesCheckEnabled
	)

	override fun render(uiState: SettingsUiState) {
		isAppUpdatesCheckEnabled?.isChecked = uiState.isAppUpdatesCheckEnabled
		isPatchUpdatesCheckEnabled?.isChecked = uiState.isPatchUpdatesCheckEnabled
	}
}