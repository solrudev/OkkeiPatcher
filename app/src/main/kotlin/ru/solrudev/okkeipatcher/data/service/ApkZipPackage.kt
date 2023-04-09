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

package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import okio.Path
import ru.solrudev.okkeipatcher.domain.service.ZipPackage

class ApkZipPackage(
	private val apkPath: Path,
	private val apkSigner: ApkSigner,
	private val ioDispatcher: CoroutineDispatcher
) : ZipPackage {

	private val zipFile = ZipFile(apkPath.toString())

	override fun close() {
		zipFile.executorService?.shutdownNow()
		zipFile.close()
	}

	override suspend fun addFiles(files: List<Path>, root: String) = runInterruptible(ioDispatcher) {
		val parameters = ZipParameters().apply { rootFolderNameInZip = root }
		zipFile.addFiles(files.map { it.toFile() }, parameters)
	}

	override suspend fun removeFiles(files: List<String>) = runInterruptible(ioDispatcher) {
		zipFile.removeFiles(files)
	}

	override suspend fun sign() {
		apkSigner.sign(apkPath.toFile())
	}

	override suspend fun removeSignature() {
		apkSigner.removeSignature(apkPath.toFile())
	}
}