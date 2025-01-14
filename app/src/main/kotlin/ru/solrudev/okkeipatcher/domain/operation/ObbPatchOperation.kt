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

package ru.solrudev.okkeipatcher.domain.operation

import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.OperationScope
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.PatchFileType
import ru.solrudev.okkeipatcher.domain.model.exception.DomainException
import ru.solrudev.okkeipatcher.domain.model.exception.ObbPatchCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.get
import ru.solrudev.okkeipatcher.domain.repository.patch.isEmpty
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion
import ru.solrudev.okkeipatcher.domain.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.util.DEFAULT_PROGRESS_MAX

class ObbPatchOperation(
	private val isUpdating: Boolean,
	private val obbPatchFiles: PatchFiles,
	externalDir: Path,
	private val obbPath: Path,
	private val obbBackupRepository: ObbBackupRepository,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : Operation<Unit> {

	private val obbPatchPath = externalDir / "obbpatch"

	private val operation = aggregateOperation(
		downloadPatches(),
		applyPatches()
	)

	override val status = operation.status
	override val messages = operation.messages
	override val progressDelta = operation.progressDelta
	override val progressMax = operation.progressMax

	override suspend fun skip() = operation.skip()

	override suspend fun invoke() = try {
		operation()
	} finally {
		fileSystem.delete(obbPatchPath)
	}

	private fun downloadPatches(): Operation<Unit> {
		val progressMultiplier = 4
		return operation(progressMax = fileDownloader.progressMax * progressMultiplier) {
			if (obbPatchFiles.isEmpty()) {
				return@operation
			}
			status(R.string.status_downloading_obb_patches)
			val obbPatchData = obbPatchFiles.get(PatchFileType.OBB_PATCH).single()
			val obbPatchHash = fileDownloader.download(
				obbPatchData.url, obbPatchPath, hashing = true,
				onProgress = { progressDelta(it * progressMultiplier) }
			)
			if (obbPatchHash != obbPatchData.hash) {
				throw ObbPatchCorruptedException()
			}
		}
	}

	private fun applyPatches(): Operation<Unit> {
		val progressMultiplier = 4
		return operation(progressMax = DEFAULT_PROGRESS_MAX * progressMultiplier) {
			if (obbPatchFiles.isEmpty() && !isUpdating) {
				restoreObbBackup(progressMultiplier)
			} else {
				applyPatches(progressMultiplier)
			}
		}
	}

	private suspend inline fun OperationScope.restoreObbBackup(progressMultiplier: Int) {
		status(R.string.status_restoring_obb)
		obbBackupRepository.restoreBackup { progressDelta ->
			progressDelta(progressDelta * progressMultiplier)
		}
	}

	private suspend inline fun OperationScope.applyPatches(progressMultiplier: Int) {
		if (obbPatchFiles.isEmpty()) {
			return
		}
		status(R.string.status_patching_obb)
		val patchedSize = obbPatchFiles.get(PatchFileType.OBB_PATCH).single().patchedSize
		obbBackupRepository.patchBackup(obbPath, obbPatchPath, patchedSize) { progressDelta ->
			progressDelta(progressDelta * progressMultiplier)
		}.onFailure { failure ->
			throw DomainException(failure.reason)
		}
		obbPatchFiles.updateInstalledVersion()
	}
}