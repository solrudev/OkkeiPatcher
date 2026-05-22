/*
 * Okkei Patcher
 * Copyright (C) 2026 Ilya Fomichev
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

package ru.solrudev.okkeipatcher.app.usecase

import ru.solrudev.okkeipatcher.app.model.OperationMode
import ru.solrudev.okkeipatcher.app.repository.OperationModeRepository
import ru.solrudev.okkeipatcher.app.usecase.OperationModeSelectionResult.Changed
import ru.solrudev.okkeipatcher.app.usecase.OperationModeSelectionResult.ShizukuServiceNotRunning
import ru.solrudev.okkeipatcher.app.usecase.OperationModeSelectionResult.Unchanged
import javax.inject.Inject

class SelectOperationModeUseCase @Inject constructor(
	private val operationModeRepository: OperationModeRepository,
	private val persistOperationModeUseCase: PersistOperationModeUseCase,
	private val checkSaveDataAccessUseCase: CheckSaveDataAccessUseCase
) {
	suspend operator fun invoke(mode: OperationMode): OperationModeSelectionResult {
		val acceptedMode = when (mode) {
			OperationMode.NonRoot -> OperationMode.NonRoot
			OperationMode.Root -> when {
				!operationModeRepository.isOperationModeSupported(mode) -> return Unchanged
				operationModeRepository.isOperationModePermissionGranted(OperationMode.Root) -> OperationMode.Root
				else -> return Unchanged
			}

			OperationMode.Shizuku -> when {
				!operationModeRepository.isOperationModeSupported(mode) -> return Unchanged
				!operationModeRepository.isShizukuServiceRunning() -> return ShizukuServiceNotRunning
				operationModeRepository.isOperationModePermissionGranted(OperationMode.Shizuku) -> OperationMode.Shizuku
				else -> return OperationModeSelectionResult.RequestShizukuPermission
			}
		}
		persistOperationModeUseCase(acceptedMode)
		checkSaveDataAccessUseCase()
		return Changed
	}
}

enum class OperationModeSelectionResult {
	Changed,
	RequestShizukuPermission,
	ShizukuServiceNotRunning,
	Unchanged
}