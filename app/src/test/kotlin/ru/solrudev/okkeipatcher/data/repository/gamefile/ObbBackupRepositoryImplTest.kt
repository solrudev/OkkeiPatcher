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
import kotlinx.coroutines.test.runTest
import okio.fakefilesystem.FakeFileSystem
import org.junit.jupiter.api.assertThrows
import ru.solrudev.okkeipatcher.data.FailingFileSystem
import ru.solrudev.okkeipatcher.data.FakePatcherEnvironment
import ru.solrudev.okkeipatcher.data.repository.FakeHashRepository
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupPath
import ru.solrudev.okkeipatcher.data.util.read
import ru.solrudev.okkeipatcher.data.util.write
import ru.solrudev.okkeipatcher.domain.model.exception.ObbNotFoundException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObbBackupRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val obb = environment.obbPath
	private val backupObb = environment.backupPath / OBB_FILE_NAME
	private val obbContent = "ObbBackupRepositoryImpl OBB test string"
	private val expectedHash = "952e311470829e5396f0884c03ee3132b2a074774f55f73df98836994fd070c1"
	private val invalidHash = "invalid APK hash"
	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()
	private val failingFileSystem = FailingFileSystem(fileSystem, allowedFunctions = listOf("metadataOrNull", "delete"))

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		hashRepository.clear()
	}

	@Test
	fun `WHEN obb backup is deleted THEN it doesn't exist`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, "some content")
		obbBackupRepository.deleteBackup()
		assertFalse(fileSystem.exists(backupObb))
	}

	@Test
	fun `backupExists returns obb backup existence`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, "some content")
		val exists = obbBackupRepository.backupExists
		fileSystem.delete(backupObb)
		val notExists = obbBackupRepository.backupExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN obb doesn't exist and backup is attempted THEN ObbNotFoundException is thrown`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.delete(obb)
		assertThrows<ObbNotFoundException> {
			obbBackupRepository.createBackup().invoke()
		}
	}

	@Test
	fun `WHEN obb backup is created THEN backup obb contains copy of installed obb`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(obb, obbContent)
		obbBackupRepository.createBackup().invoke()
		val actualContent = fileSystem.read(backupObb)
		assertEquals(obbContent, actualContent)
	}

	@Test
	fun `WHEN obb backup is created THEN backupObbHash in hash repository contains obb hash`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(obb, obbContent)
		obbBackupRepository.createBackup().invoke()
		val actualHash = hashRepository.backupObbHash.retrieve()
		assertEquals(expectedHash, actualHash)
	}

	@Test
	fun `WHEN obb backup fails with exception THEN backup obb doesn't exist`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem
		)
		fileSystem.write(obb, obbContent)
		try {
			obbBackupRepository.createBackup().invoke()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(backupObb))
	}

	@Test
	fun `WHEN backup obb exists and obb backup fails with exception THEN backup obb doesn't exist`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem
		)
		fileSystem.write(obb, obbContent)
		fileSystem.write(backupObb, obbContent)
		try {
			obbBackupRepository.createBackup().invoke()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(backupObb))
	}

	@Test
	fun `WHEN backup obb doesn't exist and restore is attempted THEN ObbNotFoundException is thrown`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.delete(backupObb)
		assertThrows<ObbNotFoundException> {
			obbBackupRepository.restoreBackup().invoke()
		}
	}

	@Test
	fun `WHEN obb backup is restored THEN obb contains copy of backup obb`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, obbContent)
		obbBackupRepository.restoreBackup().invoke()
		val actualContent = fileSystem.read(obb)
		assertEquals(obbContent, actualContent)
	}

	@Test
	fun `WHEN obb restore fails with exception THEN obb doesn't exist`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem
		)
		fileSystem.write(backupObb, obbContent)
		try {
			obbBackupRepository.restoreBackup().invoke()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(obb))
	}

	@Test
	fun `WHEN obb exists and obb restore fails with exception THEN obb doesn't exist`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, failingFileSystem
		)
		fileSystem.write(backupObb, obbContent)
		fileSystem.write(obb, obbContent)
		try {
			obbBackupRepository.restoreBackup().invoke()
		} catch (_: Throwable) {
		}
		assertFalse(fileSystem.exists(obb))
	}

	@Test
	fun `WHEN backup obb doesn't exist THEN backup obb verification fails`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.delete(backupObb)
		val isBackupObbValid = obbBackupRepository.verifyBackup().invoke()
		assertFalse(isBackupObbValid)
	}

	@Test
	fun `WHEN backup obb exists and its hash is invalid THEN backup obb verification fails`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, obbContent)
		hashRepository.backupObbHash.persist(invalidHash)
		val isBackupObbValid = obbBackupRepository.verifyBackup().invoke()
		assertFalse(isBackupObbValid)
	}

	@Test
	fun `WHEN backup obb exists and its hash is empty THEN backup obb verification fails`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, obbContent)
		val isBackupObbValid = obbBackupRepository.verifyBackup().invoke()
		assertFalse(isBackupObbValid)
	}

	@Test
	fun `WHEN backup obb exists and its hash is valid THEN backup obb verification succeeds`() = runTest {
		val obbBackupRepository = ObbBackupRepositoryImpl(
			environment, StandardTestDispatcher(testScheduler), hashRepository, fileSystem
		)
		fileSystem.write(backupObb, obbContent)
		hashRepository.backupObbHash.persist(expectedHash)
		val isBackupObbValid = obbBackupRepository.verifyBackup().invoke()
		assertTrue(isBackupObbValid)
	}
}