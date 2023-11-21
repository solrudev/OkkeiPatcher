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
import okio.buffer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.PatcherEnvironment
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupPath
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import javax.inject.Inject
import javax.inject.Singleton

const val SAVE_DATA_NAME = "SAVEDATA.DAT"
const val TEMP_SAVE_DATA_NAME = "SAVEDATA_TEMP.DAT"

@Singleton
class SaveDataRepositoryImpl @Inject constructor(
	environment: PatcherEnvironment,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val saveDataFile: SaveDataFile,
	private val hashRepository: HashRepository,
	private val fileSystem: FileSystem
) : SaveDataRepository {

	override val backupExists: Boolean
		get() = fileSystem.exists(backup)

	private val backup = environment.backupPath / SAVE_DATA_NAME
	private val temp = environment.backupPath / TEMP_SAVE_DATA_NAME

	override fun deleteBackup() {
		fileSystem.delete(backup)
	}

	override fun deleteTemp() {
		fileSystem.delete(temp)
	}

	override suspend fun createTemp(): Result<Unit> {
		val failure = Result.failure<Unit>(R.string.warning_could_not_backup_save_data)
		if (!saveDataFile.exists) {
			return failure
		}
		try {
			return runInterruptible(ioDispatcher) {
				val saveDataSource = saveDataFile.source() ?: return@runInterruptible failure
				saveDataSource.use { source ->
					fileSystem.prepareRecreate(temp)
					fileSystem.sink(temp).buffer().use { sink ->
						sink.writeAll(source)
						return@runInterruptible Result.success()
					}
				}
			}
		} catch (t: Throwable) {
			return failure
		}
	}

	override suspend fun verifyBackup(): Boolean {
		val savedHash = hashRepository.saveDataHash.retrieve()
		if (savedHash.isEmpty() || !fileSystem.exists(backup)) {
			return false
		}
		val backupHash = runInterruptible(ioDispatcher) {
			fileSystem.computeHash(backup)
		}
		return backupHash == savedHash
	}

	override suspend fun restoreBackup(): Result<Unit> {
		val failure = Result.failure<Unit>(R.string.warning_could_not_restore_save_data)
		try {
			return runInterruptible(ioDispatcher) {
				saveDataFile.recreate()
				val saveDataSink = saveDataFile.sink() ?: return@runInterruptible failure
				saveDataSink.buffer().use { sink ->
					fileSystem.source(backup).use { source ->
						sink.writeAll(source)
						return@runInterruptible Result.success()
					}
				}
			}
		} catch (t: Throwable) {
			return failure
		}
	}

	override suspend fun persistTempAsBackup() {
		if (fileSystem.exists(temp)) {
			fileSystem.delete(backup)
			fileSystem.atomicMove(temp, backup)
		}
		if (fileSystem.exists(backup)) {
			val backupHash = runInterruptible(ioDispatcher) {
				fileSystem.computeHash(backup)
			}
			hashRepository.saveDataHash.persist(backupHash)
		}
	}
}