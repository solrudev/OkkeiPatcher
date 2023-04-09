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

package ru.solrudev.okkeipatcher.ui.main.screen.update.reducer

import io.github.solrudev.jetmvi.Reducer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.model.ProgressData
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.isEmpty
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateEvent.UpdateStatusChanged
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateStatus.*
import ru.solrudev.okkeipatcher.ui.main.screen.update.model.UpdateUiState
import javax.inject.Inject

class UpdateStatusReducer @Inject constructor() : Reducer<UpdateStatusChanged, UpdateUiState> {

	override fun reduce(event: UpdateStatusChanged, state: UpdateUiState) = reduceStatus(event.updateStatus, state)

	private fun reduceStatus(status: UpdateStatus, state: UpdateUiState) = when (status) {
		is Unknown -> state
		is NoUpdate -> state.default(LocalizedString.resource(R.string.update_status_no_update))
		is UpdateAvailable -> if (!state.isUpdating && !state.isInstallPending) state.copy(
			isUpdateAvailable = true,
			isUpdating = false,
			isDownloading = false,
			isInstallPending = false,
			isInstalling = false,
			isUpdateButtonEnabled = true,
			isUpdateButtonVisible = true,
			buttonText = LocalizedString.resource(R.string.button_text_update),
			status = LocalizedString.resource(R.string.update_status_update_available),
			currentWork = null
		) else state
		is Downloading -> state.copy(
			isUpdateAvailable = false,
			isUpdating = true,
			isDownloading = true,
			isInstallPending = false,
			isInstalling = false,
			isUpdateButtonEnabled = true,
			isUpdateButtonVisible = true,
			status = LocalizedString.resource(R.string.update_status_downloading),
			progressData = status.progressData,
			percentDone = (status.progressData.progress.toDouble() / status.progressData.max * 100).toInt()
		)
		is AwaitingInstallation -> state.copy(
			isUpdateAvailable = false,
			isUpdating = false,
			isDownloading = false,
			isInstallPending = true,
			isInstalling = false,
			isUpdateButtonEnabled = true,
			isUpdateButtonVisible = true,
			buttonText = LocalizedString.resource(R.string.button_text_update_install),
			status = LocalizedString.resource(R.string.update_status_awaiting_installation),
			currentWork = null
		)
		is Installing -> state.copy(
			isUpdateAvailable = false,
			isUpdating = true,
			isDownloading = false,
			isInstallPending = false,
			isInstalling = true,
			isUpdateButtonEnabled = false,
			isUpdateButtonVisible = true,
			status = LocalizedString.resource(R.string.update_status_installing),
			currentWork = null
		)
		is Canceled -> state.default(LocalizedString.resource(R.string.update_status_canceled))
		is Failed -> state.default(
			if (status.reason.isEmpty()) {
				LocalizedString.resource(R.string.update_status_failed)
			} else {
				LocalizedString.resource(R.string.update_status_failed_with_reason, status.reason)
			}
		)
	}

	private fun UpdateUiState.default(status: LocalizedString) = copy(
		isUpdateAvailable = false,
		isUpdating = false,
		isDownloading = false,
		isInstallPending = false,
		isInstalling = false,
		isUpdateButtonEnabled = true,
		isUpdateButtonVisible = false,
		progressData = ProgressData(),
		percentDone = 0,
		buttonText = LocalizedString.resource(R.string.button_text_update),
		status = status,
		currentWork = null
	)
}