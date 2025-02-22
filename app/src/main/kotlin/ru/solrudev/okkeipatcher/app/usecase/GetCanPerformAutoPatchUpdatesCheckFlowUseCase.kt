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

package ru.solrudev.okkeipatcher.app.usecase

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.withIndex
import javax.inject.Inject

class GetCanPerformAutoPatchUpdatesCheckFlowUseCase @Inject constructor(
	private val getIsPatchUpdatesCheckEnabledFlowUseCase: GetIsPatchUpdatesCheckEnabledFlowUseCase
) {
	operator fun invoke() = getIsPatchUpdatesCheckEnabledFlowUseCase()
		.withIndex()
		.map { (index, value) -> if (index < 2) value else false }
		.take(3)
		.distinctUntilChanged()
}