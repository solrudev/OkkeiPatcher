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

package ru.solrudev.okkeipatcher.data.operation.factory

import okio.FileSystem
import ru.solrudev.okkeipatcher.data.PatcherEnvironment
import ru.solrudev.okkeipatcher.domain.operation.ObbPatchOperation
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.service.FileDownloader
import javax.inject.Inject

class ObbPatchOperationFactoryImpl @Inject constructor(
	private val obbRepository: ObbRepository,
	private val obbBackupRepository: ObbBackupRepository,
	private val environment: PatcherEnvironment,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : ObbPatchOperationFactory {

	override fun create(obbPatchFiles: PatchFiles) = ObbPatchOperation(
		obbPatchFiles,
		environment.externalFilesPath,
		obbRepository,
		obbBackupRepository,
		fileDownloader,
		fileSystem
	)
}