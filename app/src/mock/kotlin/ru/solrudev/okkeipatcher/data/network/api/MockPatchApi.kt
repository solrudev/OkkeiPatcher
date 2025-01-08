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

package ru.solrudev.okkeipatcher.data.network.api

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.data.network.api.patch.PatchApi
import ru.solrudev.okkeipatcher.data.network.model.PatchFileDto
import ru.solrudev.okkeipatcher.data.network.model.patch.PatchRequestDto
import ru.solrudev.okkeipatcher.data.network.model.patch.PatchResponseDto
import ru.solrudev.okkeipatcher.domain.model.PatchFileType
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Singleton
class MockPatchApi @Inject constructor() : PatchApi {

	private var checkCount = 0

	override suspend fun getPatchData(patchRequestDto: PatchRequestDto): PatchResponseDto {
		val delayDuration = if (checkCount > 0) 150.milliseconds else 2.seconds
		val isUpdateAvailable = checkCount++ % 2 == 0
		delay(delayDuration)
		return PatchResponseDto(
			displayVersion = "1.0(mock)",
			apk = listOf(
				PatchFileDto(
					type = PatchFileType.SCRIPTS,
					version = if (isUpdateAvailable) 2 else 1,
					url = "",
					hash = "",
					size = 2000000,
					patchedSize = -1,
					compatibleHashes = emptyList()
				)
			),
			obb = listOf(
				PatchFileDto(
					type = PatchFileType.OBB_PATCH,
					version = if (isUpdateAvailable) 2 else 1,
					url = "",
					hash = "",
					size = 90000000,
					patchedSize = 1700000000,
					compatibleHashes = emptyList()
				)
			)
		)
	}
}