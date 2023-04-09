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

package ru.solrudev.okkeipatcher.data.repository.patch

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class MockPatchRepository @Inject constructor() : PatchRepository {

	private var checkCount = 0

	override suspend fun getDisplayVersion() = "1.0(mock)"

	override suspend fun getPatchUpdates(refresh: Boolean): PatchUpdates {
		delay(3.seconds)
		val isUpdateAvailable = checkCount++ % 2 == 0
		return DefaultPatchUpdates(isUpdateAvailable)
	}

	override suspend fun getPatchSizeInMb(): Double {
		delay(1.seconds)
		return 100.0
	}

	override suspend fun clearPersistedData() {}
}