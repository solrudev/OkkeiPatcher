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
import ru.solrudev.okkeipatcher.data.util.read
import ru.solrudev.okkeipatcher.data.util.write
import ru.solrudev.okkeipatcher.domain.core.Result
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SaveDataRepositoryImplTest {

	private val environment = FakePatcherEnvironment()
	private val saveData = environment.saveDataPath
	private val backupSaveData = environment.backupPath / SAVE_DATA_NAME
	private val tempSaveData = environment.backupPath / TEMP_SAVE_DATA_NAME
	private val saveDataContent = "SaveDataRepositoryImpl save data test string"
	private val expectedHash = "92799fc4b3dd7ff1810f25d32fc0a673c964a1ac74dc0c816bbc1506ee9af066"
	private val invalidHash = "invalid save data hash"
	private val hashRepository = FakeHashRepository()
	private val fileSystem = FakeFileSystem()
	private val failingFileSystem = FailingFileSystem(fileSystem, allowedFunctions = listOf("metadataOrNull", "delete"))
	private val saveDataFile = SaveDataRawFile(environment, fileSystem)
	private val testScope = TestScope()
	private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)

	private val saveDataRepository = SaveDataRepositoryImpl(
		environment, testDispatcher, saveDataFile, hashRepository, fileSystem
	)

	private val failingSaveDataRepository = SaveDataRepositoryImpl(
		environment, testDispatcher, saveDataFile, hashRepository, failingFileSystem
	)

	@AfterTest
	fun tearDown() = runBlocking {
		fileSystem.checkNoOpenFiles()
		fileSystem.delete(saveData)
		fileSystem.delete(tempSaveData)
		fileSystem.delete(backupSaveData)
		hashRepository.clear()
	}

	@Test
	fun `WHEN save data backup is deleted THEN it doesn't exist`() {
		fileSystem.write(backupSaveData, "some content")
		saveDataRepository.deleteBackup()
		assertFalse(fileSystem.exists(backupSaveData))
	}

	@Test
	fun `WHEN temp save data is deleted THEN it doesn't exist`() {
		fileSystem.write(tempSaveData, "some content")
		saveDataRepository.deleteTemp()
		assertFalse(fileSystem.exists(tempSaveData))
	}

	@Test
	fun `backupExists returns save data backup existence`() {
		fileSystem.write(backupSaveData, "some content")
		val exists = saveDataRepository.backupExists
		fileSystem.delete(backupSaveData)
		val notExists = saveDataRepository.backupExists
		assertTrue(exists)
		assertFalse(notExists)
	}

	@Test
	fun `WHEN temp save data is created THEN temp save data contains copy of game save data`() = testScope.runTest {
		fileSystem.write(saveData, saveDataContent)
		saveDataRepository.createTemp()
		val actualContent = fileSystem.read(tempSaveData)
		assertEquals(saveDataContent, actualContent)
	}

	@Test
	fun `WHEN temp save data is created THEN success is returned`() = testScope.runTest {
		fileSystem.write(saveData, saveDataContent)
		val result = saveDataRepository.createTemp()
		assertIs<Result.Success<*>>(result)
	}

	@Test
	fun `WHEN save data doesn't exist and temp copy creation is attempted THEN failure is returned`() =
		testScope.runTest {
			val result = saveDataRepository.createTemp()
			assertIs<Result.Failure<*>>(result)
		}

	@Test
	fun `WHEN save data temp copy creation fails THEN failure is returned`() = testScope.runTest {
		fileSystem.write(saveData, saveDataContent)
		val result = failingSaveDataRepository.createTemp()
		assertIs<Result.Failure<*>>(result)
	}

	@Test
	fun `WHEN save data backup is restored THEN game save data contains copy of backup save data`() =
		testScope.runTest {
			fileSystem.write(backupSaveData, saveDataContent)
			saveDataRepository.restoreBackup()
			val actualContent = fileSystem.read(saveData)
			assertEquals(saveDataContent, actualContent)
		}

	@Test
	fun `WHEN save data backup is restored THEN success is returned`() = testScope.runTest {
		fileSystem.write(backupSaveData, saveDataContent)
		val result = saveDataRepository.restoreBackup()
		assertIs<Result.Success<*>>(result)
	}

	@Test
	fun `WHEN save data backup doesn't exist and backup restore is attempted THEN failure is returned`() =
		testScope.runTest {
			val result = saveDataRepository.restoreBackup()
			assertIs<Result.Failure<*>>(result)
		}

	@Test
	fun `WHEN save data backup restore fails THEN failure is returned`() = testScope.runTest {
		fileSystem.write(backupSaveData, saveDataContent)
		val result = failingSaveDataRepository.restoreBackup()
		assertIs<Result.Failure<*>>(result)
	}

	@Test
	fun `WHEN backup save data doesn't exist THEN backup save data verification fails`() = testScope.runTest {
		val isBackupSaveDataValid = saveDataRepository.verifyBackup()
		assertFalse(isBackupSaveDataValid)
	}

	@Test
	fun `WHEN backup save data exists and its hash is invalid THEN backup save data verification fails`() =
		testScope.runTest {
			fileSystem.write(backupSaveData, saveDataContent)
			hashRepository.saveDataHash.persist(invalidHash)
			val isBackupSaveDataValid = saveDataRepository.verifyBackup()
			assertFalse(isBackupSaveDataValid)
		}

	@Test
	fun `WHEN backup save data exists and its hash is empty THEN backup save data verification fails`() =
		testScope.runTest {
			fileSystem.write(backupSaveData, saveDataContent)
			val isBackupSaveDataValid = saveDataRepository.verifyBackup()
			assertFalse(isBackupSaveDataValid)
		}

	@Test
	fun `WHEN backup save data exists and its hash is valid THEN backup save data verification succeeds`() =
		testScope.runTest {
			fileSystem.write(backupSaveData, saveDataContent)
			hashRepository.saveDataHash.persist(expectedHash)
			val isBackupSaveDataValid = saveDataRepository.verifyBackup()
			assertTrue(isBackupSaveDataValid)
		}

	@Test
	fun `WHEN temp save data exists THEN persistTempAsBackup moves content to backup save data`() = testScope.runTest {
		fileSystem.write(tempSaveData, saveDataContent)
		saveDataRepository.persistTempAsBackup()
		val actualBackupContent = fileSystem.read(backupSaveData)
		assertEquals(saveDataContent, actualBackupContent)
		assertFalse(fileSystem.exists(tempSaveData))
	}

	@Test
	fun `WHEN temp save data exists and backup exists THEN persistTempAsBackup overwrites backup save data content`() =
		testScope.runTest {
			fileSystem.write(tempSaveData, saveDataContent)
			fileSystem.write(backupSaveData, "some content")
			saveDataRepository.persistTempAsBackup()
			val actualBackupContent = fileSystem.read(backupSaveData)
			assertEquals(saveDataContent, actualBackupContent)
		}

	@Test
	fun `WHEN persisting temp save data as backup THEN saveDataHash in hash repository contains save data hash`() =
		testScope.runTest {
			fileSystem.write(tempSaveData, saveDataContent)
			saveDataRepository.persistTempAsBackup()
			val actualHash = hashRepository.saveDataHash.retrieve()
			assertEquals(expectedHash, actualHash)
		}

	@Test
	fun `WHEN temp doesn't exist and backup exists THEN persistTempAsBackup saves backup hash into saveDataHash in hash repository`() =
		testScope.runTest {
			fileSystem.write(backupSaveData, saveDataContent)
			saveDataRepository.persistTempAsBackup()
			val actualHash = hashRepository.saveDataHash.retrieve()
			assertEquals(expectedHash, actualHash)
		}

	@Test
	fun `WHEN temp and backup save data don't exist THEN persistTempAsBackup does nothing`() = testScope.runTest {
		saveDataRepository.persistTempAsBackup()
		val actualHash = hashRepository.saveDataHash.retrieve()
		assertEquals("", actualHash)
		assertFalse(fileSystem.exists(saveData))
		assertFalse(fileSystem.exists(tempSaveData))
		assertFalse(fileSystem.exists(backupSaveData))
	}
}