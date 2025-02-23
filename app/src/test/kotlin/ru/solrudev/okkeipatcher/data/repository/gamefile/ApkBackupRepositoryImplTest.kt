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
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupPath
import ru.solrudev.okkeipatcher.data.service.FakeGameInstallationProvider
import ru.solrudev.okkeipatcher.data.service.FakePackageInstallerFacade
import ru.solrudev.okkeipatcher.data.util.read
import ru.solrudev.okkeipatcher.data.util.write
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ApkBackupRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val backupApk = environment.backupPath / "backup.apk"
	private val installedApkContent = "ApkBackupRepositoryImpl installed APK test string"
	private val expectedHash = "2ab5b28f253c33adc5b7830e4b7620cbfb4c2c550daf3bf32887b5931a44830d"
	private val invalidHash = "invalid APK hash"
	private val gameInstallationProvider = FakeGameInstallationProvider()
	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()
	private val failingFileSystem = FailingFileSystem(fileSystem, allowedFunctions = listOf("delete"))
	private val packageInstaller = FakePackageInstallerFacade()
	private val testScope = TestScope()
	private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

	private val apkBackupRepository = ApkBackupRepositoryImpl(
		environment, gameInstallationProvider, testDispatcher, packageInstaller, hashRepository, fileSystem
	)

	private val failingApkBackupRepository = ApkBackupRepositoryImpl(
		environment, gameInstallationProvider, testDispatcher, packageInstaller, hashRepository, failingFileSystem
	)

	@BeforeTest
	fun setUp() {
		fileSystem.write(gameInstallationProvider.getApkPath(), installedApkContent)
		fileSystem.delete(backupApk)
	}

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `WHEN apk backup is deleted THEN it doesn't exist`() {
		fileSystem.write(backupApk, "some content")
		apkBackupRepository.deleteBackup()
		assertFalse(fileSystem.exists(backupApk))
	}

	@Test
	fun `backupExists returns apk backup existence`() {
		fileSystem.write(backupApk, "some content")
		val exists = apkBackupRepository.backupExists
		fileSystem.delete(backupApk)
		val notExists = apkBackupRepository.backupExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN apk backup is created THEN backup apk contains copy of installed apk`() = testScope.runTest {
		apkBackupRepository.createBackup()
		val backupContent = fileSystem.read(backupApk)
		assertEquals(installedApkContent, backupContent)
	}

	@Test
	fun `WHEN apk backup is created THEN backupApkHash in hash repository contains apk hash`() = testScope.runTest {
		apkBackupRepository.createBackup()
		val actualHash = hashRepository.backupApkHash.retrieve()
		assertEquals(expectedHash, actualHash)
	}

	@Test
	fun `createBackup returns apk hash`() = testScope.runTest {
		val actualHash = apkBackupRepository.createBackup()
		assertEquals(expectedHash, actualHash)
	}

	@Test
	fun `WHEN apk backup fails with exception THEN backup apk doesn't exist`() = testScope.runTest {
		runCatching {
			failingApkBackupRepository.createBackup()
		}
		assertFalse(fileSystem.exists(backupApk))
	}

	@Test
	fun `WHEN backup apk exists and apk backup fails with exception THEN backup apk doesn't exist`() =
		testScope.runTest {
			fileSystem.write(backupApk, installedApkContent)
			runCatching {
				failingApkBackupRepository.createBackup()
			}
			assertFalse(fileSystem.exists(backupApk))
		}

	@Test
	fun `WHEN backup apk doesn't exist THEN backup apk verification fails`() = testScope.runTest {
		val isBackupApkValid = apkBackupRepository.verifyBackup()
		assertFalse(isBackupApkValid)
	}

	@Test
	fun `WHEN backup apk exists and its hash is invalid THEN backup apk verification fails`() = testScope.runTest {
		fileSystem.write(backupApk, installedApkContent)
		hashRepository.backupApkHash.persist(invalidHash)
		val isBackupApkValid = apkBackupRepository.verifyBackup()
		assertFalse(isBackupApkValid)
	}

	@Test
	fun `WHEN backup apk exists and its hash is empty THEN backup apk verification fails`() = testScope.runTest {
		fileSystem.write(backupApk, installedApkContent)
		val isBackupApkValid = apkBackupRepository.verifyBackup()
		assertFalse(isBackupApkValid)
	}

	@Test
	fun `WHEN backup apk exists and its hash is valid THEN backup apk verification succeeds`() = testScope.runTest {
		fileSystem.write(backupApk, installedApkContent)
		hashRepository.backupApkHash.persist(expectedHash)
		val isBackupApkValid = apkBackupRepository.verifyBackup()
		assertTrue(isBackupApkValid)
	}
}