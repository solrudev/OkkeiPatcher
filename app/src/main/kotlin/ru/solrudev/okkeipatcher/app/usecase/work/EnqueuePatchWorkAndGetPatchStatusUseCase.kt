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

package ru.solrudev.okkeipatcher.app.usecase.work

import kotlinx.coroutines.flow.first
import ru.solrudev.okkeipatcher.app.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.app.usecase.patch.GetPatchStatusFlowUseCase
import javax.inject.Inject

class EnqueuePatchWorkAndGetPatchStatusUseCase @Inject constructor(
	private val patchWorkRepository: PatchWorkRepository,
	private val getPatchStatusFlowUseCase: GetPatchStatusFlowUseCase
) {

	/**
	 * Returns current patch status.
	 */
	suspend operator fun invoke(): Boolean {
		patchWorkRepository.enqueueWork()
		return getPatchStatusFlowUseCase().first()
	}
}