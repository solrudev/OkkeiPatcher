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

import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.PatcherEnvironment
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.operation.ApkPatchOperation
import ru.solrudev.okkeipatcher.domain.operation.factory.ApkPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFiles
import ru.solrudev.okkeipatcher.domain.service.FileDownloader
import javax.inject.Inject

class ApkPatchOperationFactoryImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val environment: PatcherEnvironment,
	private val apkRepository: ApkRepository,
	private val hashRepository: HashRepository,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : ApkPatchOperationFactory {

	override fun create(scriptsPatchFiles: PatchFiles) = ApkPatchOperation(
		scriptsPatchFiles,
		apkRepository,
		hashRepository.signedApkHash,
		ioDispatcher,
		environment.externalFilesPath,
		fileDownloader,
		fileSystem
	)
}