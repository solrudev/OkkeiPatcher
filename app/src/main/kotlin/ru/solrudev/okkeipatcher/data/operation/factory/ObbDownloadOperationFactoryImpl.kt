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

import ru.solrudev.okkeipatcher.data.operation.ObbDownloadOperation
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.operation.factory.ObbDownloadOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import javax.inject.Inject

class ObbDownloadOperationFactoryImpl @Inject constructor(
	private val obbRepository: ObbRepository,
	private val fileDownloader: FileDownloader
) : ObbDownloadOperationFactory {

	override fun create(obbPatchFile: PatchFile) = ObbDownloadOperation(obbPatchFile, obbRepository, fileDownloader)
}