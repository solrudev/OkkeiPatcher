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

package ru.solrudev.okkeipatcher.data.operation

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.FileSystem
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.PatcherEnvironment
import ru.solrudev.okkeipatcher.data.repository.gamefile.OBB_FILE_NAME
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.asOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.PatchFileType
import ru.solrudev.okkeipatcher.domain.model.exception.ObbPatchCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion
import kotlin.time.Duration.Companion.seconds

class ObbPatchOperation(
	private val obbPatchFiles: PatchFiles,
	environment: PatcherEnvironment,
	private val obbRepository: ObbRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : Operation<Unit> {

	private val operation = aggregateOperation(
		downloadPatches(),
		applyPatches(),
		replaceObb()
	)

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax
	private val obbPatchPath = environment.externalFilesPath / "obbpatch"
	private val patchedObbPath = environment.externalFilesPath / OBB_FILE_NAME

	override suspend fun skip() = operation.skip()

	override suspend fun invoke() = try {
		operation()
	} finally {
		fileSystem.delete(patchedObbPath)
		fileSystem.delete(obbPatchPath)
	}

	private fun downloadPatches(): Operation<Unit> {
		val progressMultiplier = 3
		return operation(progressMax = fileDownloader.progressMax * progressMultiplier) {
			status(R.string.status_downloading_obb_patches)
			val obbPatchData = obbPatchFiles.getData().single { it.type == PatchFileType.OBB_PATCH }
			val obbHash = fileDownloader.download(
				obbPatchData.url, obbPatchPath, hashing = true,
				onProgressChanged = { progressDelta(it * progressMultiplier) }
			)
			if (obbHash != obbPatchData.hash) {
				throw ObbPatchCorruptedException()
			}
		}
	}

	private fun applyPatches(): Operation<Unit> = operation(progressMax = 200) {
		status(R.string.status_patching_obb)
		coroutineScope {
			launch {
				repeat(4) {
					delay(5.seconds)
					progressDelta(50)
				}
			}
			obbBackupRepository.patchBackup(patchedObbPath, obbPatchPath)
		}
	}

	private fun replaceObb() = aggregateOperation(
		obbRepository.copyFrom(patchedObbPath).asOperation(),
		operation {
			obbPatchFiles.updateInstalledVersion()
		}
	)
}