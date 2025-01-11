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
	private val testScope = TestScope()
	private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

	private val apkRepository = ApkRepositoryImpl(
		environment, gameInstallationProvider, testDispatcher, packageInstaller,
		hashRepository, apkZipPackageFactory, fileSystem
	)

	private val failingApkRepository = ApkRepositoryImpl(
		environment, gameInstallationProvider, testDispatcher, packageInstaller,
		hashRepository, apkZipPackageFactory, failingFileSystem
	)

	@BeforeTest
	fun setUp() {
		fileSystem.write(gameInstallationProvider.getApkPath(), installedApkContent)
		fileSystem.delete(tempApk)
	}

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `WHEN temp apk is deleted THEN it doesn't exist`() {
		fileSystem.write(tempApk, "some content")
		apkRepository.deleteTemp()
		assertFalse(fileSystem.exists(tempApk))
	}

	@Test
	fun `tempExists returns temp apk existence`() {
		fileSystem.write(tempApk, "some content")
		val exists = apkRepository.tempExists
		fileSystem.delete(tempApk)
		val notExists = apkRepository.tempExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN apk temp copy is created THEN temp apk contains copy of installed apk`() = testScope.runTest {
		apkRepository.getTemp()
		val tempContent = fileSystem.read(tempApk)
		assertEquals(installedApkContent, tempContent)
	}

	@Test
	fun `WHEN apk temp copy creation fails with exception THEN temp apk doesn't exist`() = testScope.runTest {
		runCatching {
			failingApkRepository.getTemp()
		}
		assertFalse(fileSystem.exists(tempApk))
	}

	@Test
	fun `WHEN temp apk exists and apk temp copy creation is attempted THEN temp apk remains unchanged`() =
		testScope.runTest {
			val expectedContent = "some arbitrary content"
			fileSystem.write(tempApk, expectedContent)
			apkRepository.getTemp()
			val actualContent = fileSystem.read(tempApk)
			assertEquals(expectedContent, actualContent)
		}

	@Test
	fun `WHEN temp apk exists and apk temp copy creation fails with exception THEN temp apk doesn't exist`() =
		testScope.runTest {
			fileSystem.write(tempApk, installedApkContent)
			runCatching {
				failingApkRepository.getTemp()
			}
			assertFalse(fileSystem.exists(tempApk))
		}

	@Test
	fun `WHEN temp apk doesn't exist THEN temp apk verification fails`() = testScope.runTest {
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and signed apk hash is invalid THEN temp apk verification fails`() = testScope.runTest {
		fileSystem.write(tempApk, installedApkContent)
		hashRepository.signedApkHash.persist(invalidHash)
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and its hash is empty THEN temp apk verification fails`() = testScope.runTest {
		fileSystem.write(tempApk, installedApkContent)
		val isTempApkValid = apkRepository.verifyTemp()
		assertFalse(isTempApkValid)
	}

	@Test
	fun `WHEN temp apk exists and signed apk hash is valid THEN temp apk verification succeeds`() = testScope.runTest {
		fileSystem.write(tempApk, installedApkContent)
		hashRepository.signedApkHash.persist(expectedHash)
		val isTempApkValid = apkRepository.verifyTemp()
		assertTrue(isTempApkValid)
	}
}