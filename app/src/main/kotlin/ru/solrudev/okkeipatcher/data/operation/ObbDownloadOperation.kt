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

package ru.solrudev.okkeipatcher.data.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.core.operation.status
import ru.solrudev.okkeipatcher.domain.model.exception.ObbCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.repository.patch.updateInstalledVersion

private const val PROGRESS_MULTIPLIER = 10

@Suppress("FunctionName")
fun ObbDownloadOperation(
	obbPatchFile: PatchFile,
	obbRepository: ObbRepository,
	fileDownloader: FileDownloader
) = operation(progressMax = fileDownloader.progressMax * PROGRESS_MULTIPLIER) {
	try {
		status(R.string.status_downloading_obb)
		val obbData = obbPatchFile.getData()
		val obbHash = fileDownloader.download(
			obbData.url, obbRepository.obbPath, hashing = true,
			onProgressDeltaChanged = { progressDelta(it * PROGRESS_MULTIPLIER) }
		)
		if (obbHash != obbData.hash) {
			throw ObbCorruptedException()
		}
		obbPatchFile.updateInstalledVersion()
	} catch (t: Throwable) {
		obbRepository.deleteObb()
		throw t
	}
}