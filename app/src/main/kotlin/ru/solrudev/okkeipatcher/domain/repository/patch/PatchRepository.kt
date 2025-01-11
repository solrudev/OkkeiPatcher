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

package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.domain.core.persistence.Dao
import ru.solrudev.okkeipatcher.domain.model.PatchFileData
import ru.solrudev.okkeipatcher.domain.model.PatchUpdates

interface PatchRepository {
	val apkPatchFiles: PatchFiles
	val obbPatchFiles: PatchFiles
	suspend fun getDisplayVersion(): String
	suspend fun getPatchUpdates(refresh: Boolean = false): PatchUpdates
	suspend fun getPatchSizeInMb(): Double
	suspend fun clearPersistedData()
}

interface PatchFiles {
	val installedVersion: Dao<Int>
	suspend fun getData(refresh: Boolean = false): List<PatchFileData>
	suspend fun isUpdateAvailable(refresh: Boolean = false): Boolean
	suspend fun getSizeInMb(): Double
}

suspend inline fun PatchFiles.updateInstalledVersion() {
	val newVersion = getData().maxOfOrNull { it.version } ?: return
	installedVersion.persist(newVersion)
}

suspend inline fun PatchFiles.isCompatible(hash: String) = getData()
	.map { it.compatibleHashes }
	.flatten()
	.any { it == hash }