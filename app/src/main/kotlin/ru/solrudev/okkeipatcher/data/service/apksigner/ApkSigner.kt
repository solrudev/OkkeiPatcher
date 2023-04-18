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

package ru.solrudev.okkeipatcher.data.service.apksigner

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import net.lingala.zip4j.ZipFile
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.data.service.util.use
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import javax.inject.Inject

interface ApkSigner {
	suspend fun sign(apkPath: Path)
	suspend fun removeSignature(apkPath: Path)
}

class ApkSignerWrapper @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val hashRepository: HashRepository,
	private val fileSystem: FileSystem,
	private val apkSigner: ApkSignerImplementation
) : ApkSigner {

	override suspend fun sign(apkPath: Path) {
		val outputApk = apkPath.parent!! / "${apkPath.name.substringBeforeLast('.')}-signed.apk"
		try {
			apkSigner.sign(apkPath, outputApk)
			val outputApkHash = runInterruptible(ioDispatcher) {
				fileSystem.computeHash(outputApk)
			}
			hashRepository.signedApkHash.persist(outputApkHash)
			fileSystem.atomicMove(outputApk, apkPath)
		} catch (t: Throwable) {
			fileSystem.delete(outputApk)
			throw t
		}
	}

	override suspend fun removeSignature(apkPath: Path) = runInterruptible(ioDispatcher) {
		ZipFile(apkPath.toString()).use { zipFile ->
			val headers = zipFile.fileHeaders
				.filter { it.fileName.startsWith("META-INF/", ignoreCase = true) }
				.map { it.fileName }
			zipFile.removeFiles(headers)
		}
	}
}