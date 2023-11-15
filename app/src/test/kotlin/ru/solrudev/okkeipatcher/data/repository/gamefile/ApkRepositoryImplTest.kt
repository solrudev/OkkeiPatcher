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

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.fakefilesystem.FakeFileSystem
import ru.solrudev.okkeipatcher.data.FailingFileSystem
import ru.solrudev.okkeipatcher.data.FakePatcherEnvironment
import ru.solrudev.okkeipatcher.data.repository.FakeHashRepository
import ru.solrudev.okkeipatcher.data.service.FakeGameInstallationProvider
import ru.solrudev.okkeipatcher.data.service.FakePackageInstallerFacade
import ru.solrudev.okkeipatcher.data.service.factory.MockZipPackageFactory
import ru.solrudev.okkeipatcher.data.util.read
import ru.solrudev.okkeipatcher.data.util.write
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApkRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val tempApk = environment.externalFilesPath / "temp.apk"
	private val installedApkContent = "ApkRepositoryImpl installed APK test string"
	private val expectedHash = "ea106a2eb57bcc729b11a261e8008a57d6e052dc71c73a169ecb68b0996465aa"
	private val invalidHash = "invalid APK hash"
	private val apkZipPackageFactory = MockZipPackageFactory()
	private val gameInstallationProvider = FakeGameInstallationProvider()
	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()
	private val failingFileSystem = FailingFileSystem(fileSystem, allowedFunctions = listOf("delete"))
	private val packageInstaller = FakePackageInstallerFacade()

	@BeforeTest
	fun setUp() {
		fileSystem.write(gameInstallationProvider.getApkPath(), installedApkContent)
	}

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `WHEN temp apk is deleted THEN it doesn't exist`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.write(tempApk, "some content")
		apkRepository.deleteTemp()
		assertFalse(fileSystem.exists(tempApk))
	}

	@Test
	fun `tempExists returns temp apk existence`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.write(tempApk, "some content")
		val exists = apkRepository.tempExists
		fileSystem.delete(tempApk)
		val notExists = apkRepository.tempExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN apk temp copy is created THEN temp apk contains copy of installed apk`() = runTest {
		val apkRepository = apkRepositoryImpl()
		apkRepository.createTemp()
		val tempContent = fileSystem.read(tempApk)
		assertEquals(installedApkContent, tempContent)
	}

	@Test
	fun `WHEN apk temp copy creation fails with exception THEN temp apk doesn't exist`() = runTest {
		val apkRepository = apkRepositoryImpl(failingFileSystem)
		try {
			apkRepository.createTemp()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(tempApk))
	}

	@Test
	fun `WHEN temp apk exists and apk temp copy creation is attempted THEN temp apk remains unchanged`() = runTest {
		val apkRepository = apkRepositoryImpl()
		val expectedContent = "some arbitrary content"
		fileSystem.write(tempApk, expectedContent)
		apkRepository.createTemp()
		val actualContent = fileSystem.read(tempApk)
		assertEquals(expectedContent, actualContent)
	}

	@Test
	fun `WHEN temp apk exists and apk temp copy creation fails with exception THEN temp apk doesn't exist`() = runTest {
		val apkRepository = apkRepositoryImpl(failingFileSystem)
		fileSystem.write(tempApk, installedApkContent)
		try {
			apkRepository.createTemp()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(tempApk))
	}

	@Test
	fun `WHEN temp apk doesn't exist THEN temp apk verification fails`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.delete(tempApk)
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and signed apk hash is invalid THEN temp apk verification fails`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.write(tempApk, installedApkContent)
		hashRepository.signedApkHash.persist(invalidHash)
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and its hash is empty THEN temp apk verification fails`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.write(tempApk, installedApkContent)
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and signed apk hash is valid THEN temp apk verification succeeds`() = runTest {
		val apkRepository = apkRepositoryImpl()
		fileSystem.write(tempApk, installedApkContent)
		hashRepository.signedApkHash.persist(expectedHash)
		val isTempApkValid = apkRepository.verifyTemp()
		assertTrue(isTempApkValid)
	}

	private fun TestScope.apkRepositoryImpl(
		fileSystem: FileSystem = this@ApkRepositoryImplTest.fileSystem
	) = ApkRepositoryImpl(
		environment, gameInstallationProvider, StandardTestDispatcher(testScheduler), packageInstaller,
		hashRepository, apkZipPackageFactory, fileSystem
	)
}