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

package ru.solrudev.okkeipatcher.data.repository.gamefile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.data.PatcherEnvironment
import ru.solrudev.okkeipatcher.data.service.GameInstallationProvider
import ru.solrudev.okkeipatcher.data.service.PackageInstallerFacade
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.util.GAME_NAME
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.service.ZipPackage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkRepositoryImpl @Inject constructor(
	environment: PatcherEnvironment,
	private val gameInstallationProvider: GameInstallationProvider,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val packageInstaller: PackageInstallerFacade,
	private val hashRepository: HashRepository,
	private val apkZipPackageFactory: ApkZipPackageFactory,
	private val fileSystem: FileSystem
) : ApkRepository {

	override val isInstalled: Boolean
		get() = gameInstallationProvider.isInstalled()

	override val tempExists: Boolean
		get() = fileSystem.exists(temp)

	private val temp = environment.externalFilesPath / "temp.apk"

	private val installed: Path
		get() = gameInstallationProvider.getApkPath()

	override fun deleteTemp() {
		fileSystem.delete(temp)
	}

	override suspend fun createTemp(): ZipPackage {
		try {
			if (!fileSystem.exists(temp)) {
				runInterruptible(ioDispatcher) {
					fileSystem.copy(installed, temp)
				}
			}
			return apkZipPackageFactory.create(temp)
		} catch (t: Throwable) {
			fileSystem.delete(temp)
			throw t
		}
	}

	override suspend fun verifyTemp(): Boolean {
		if (!fileSystem.exists(temp)) {
			return false
		}
		val savedHash = hashRepository.signedApkHash.retrieve()
		if (savedHash.isEmpty()) {
			return false
		}
		val fileHash = runInterruptible(ioDispatcher) {
			fileSystem.computeHash(temp)
		}
		return fileHash == savedHash
	}

	override suspend fun installTemp() = packageInstaller.install(temp, appName = GAME_NAME)
	override suspend fun uninstall() = packageInstaller.uninstall(GAME_PACKAGE_NAME)
}