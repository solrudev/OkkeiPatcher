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

package ru.solrudev.okkeipatcher.ui.main.screen.settings.controller

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.navigation.NavController
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import io.github.solrudev.jetmvi.JetView
import rikka.shizuku.Shizuku
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsFragmentDirections
import ru.solrudev.okkeipatcher.ui.main.screen.settings.SettingsViewModel
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.HandleSaveDataToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.SaveDataAccessRequestHandled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionGranted
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuPermissionRequestHandled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsEvent.ShizukuToggled
import ru.solrudev.okkeipatcher.ui.main.screen.settings.model.SettingsUiState
import ru.solrudev.okkeipatcher.ui.main.util.HapticFeedbackCallback
import ru.solrudev.okkeipatcher.ui.util.navigateSafely

private const val SHIZUKU_PERMISSION_REQUEST_CODE = 0

class PatcherSettingsController(
	private val handleSaveData: SwitchPreferenceCompat?,
	private val shizuku: SwitchPreferenceCompat?,
	clearData: Preference?,
	private val navController: NavController,
	private val viewModel: SettingsViewModel,
	private val hapticFeedbackCallback: HapticFeedbackCallback
) : JetView<SettingsUiState> {

	private val shizukuPermissionListener = Shizuku.OnRequestPermissionResultListener { requestCode, result ->
		if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE && result == PERMISSION_GRANTED) {
			viewModel.dispatchEvent(ShizukuPermissionGranted)
		}
	}

	init {
		handleSaveData?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(HandleSaveDataToggled)
			hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
			false
		}
		shizuku?.setOnPreferenceClickListener {
			viewModel.dispatchEvent(ShizukuToggled)
			hapticFeedbackCallback.performHapticFeedback(HapticFeedbackConstantsCompat.CONTEXT_CLICK)
			false
		}
		clearData?.setOnPreferenceClickListener {
			navigateToClearDataScreen()
			true
		}
		if (Build.VERSION.SDK_INT >= 24) {
			Shizuku.addRequestPermissionResultListener(shizukuPermissionListener)
		}
	}

	override val trackedState = listOf(
		SettingsUiState::handleSaveData,
		SettingsUiState::isShizukuEnabled,
		SettingsUiState::requestSaveDataAccess,
		SettingsUiState::requestShizukuPermission
	)

	override fun render(uiState: SettingsUiState) {
		handleSaveData?.isChecked = uiState.handleSaveData
		shizuku?.isChecked = uiState.isShizukuEnabled
		if (uiState.requestSaveDataAccess) {
			navigateToSaveDataAccessScreen()
		}
		if (uiState.requestShizukuPermission) {
			requestShizukuPermission()
		}
	}

	fun removeShizukuRequestPermissionResultListener() = try {
		Shizuku.removeRequestPermissionResultListener(shizukuPermissionListener)
	} catch (_: NoClassDefFoundError) { // ignore
	}

	private fun requestShizukuPermission() {
		viewModel.dispatchEvent(ShizukuPermissionRequestHandled)
		try {
			Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
		} catch (_: Exception) { // ignore
		}
	}

	private fun navigateToSaveDataAccessScreen() {
		viewModel.dispatchEvent(SaveDataAccessRequestHandled)
		val toSaveDataAccessScreen = SettingsFragmentDirections.actionSettingsFragmentToSaveDataAccessFragment()
		navController.navigateSafely(toSaveDataAccessScreen)
	}

	private fun navigateToClearDataScreen() {
		val toClearDataScreen = SettingsFragmentDirections.actionSettingsFragmentToClearDataFragment()
		navController.navigateSafely(toClearDataScreen)
	}
}