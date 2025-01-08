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
import okio.Path
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.core.onFailure
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.PatchFileType
import ru.solrudev.okkeipatcher.domain.model.exception.DomainException
import ru.solrudev.okkeipatcher.domain.model.exception.ObbPatchCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

class ObbPatchOperation(
	private val obbPatchFiles: PatchFiles,
	externalDir: Path,
	obbRepository: ObbRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : Operation<Unit> {

	private val obbPatchPath = externalDir / "obbpatch"
	private val obbPath = obbRepository.obbPath

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
			status(R.string.status_downloading_obb_patches)
			val obbPatchData = obbPatchFiles
				.getData()
				.single { it.type == PatchFileType.OBB_PATCH }
			val obbHash = fileDownloader.download(
				obbPatchData.url, obbPatchPath, hashing = true,
				onProgressChanged = { progressDelta(it * progressMultiplier) }
			)
			if (obbHash != obbPatchData.hash) {
				throw ObbPatchCorruptedException()
			}
		}
	}

	private fun applyPatches(): Operation<Unit> {
		val progressMultiplier = 4
		return operation(progressMax = 100 * progressMultiplier) {
			status(R.string.status_patching_obb)
			coroutineScope {
				val progressJob = launch {
					val size = obbPatchFiles
						.getData()
						.single { it.type == PatchFileType.OBB_PATCH }
						.patchedSize
					var previousSize = 0L
					while (true) {
						delay(2.seconds)
						val currentSize = fileSystem.metadataOrNull(obbPath)?.size ?: 0
						val delta = currentSize - previousSize
						previousSize = currentSize
						val normalizedDelta = (delta.toDouble() / size * 100).roundToInt()
						progressDelta(normalizedDelta * progressMultiplier)
					}
				}
				obbBackupRepository.patchBackup(obbPath, obbPatchPath).onFailure { failure ->
					throw DomainException(failure.reason)
				}
				progressJob.cancel()
				obbPatchFiles.updateInstalledVersion()
			}
		}
	}
}