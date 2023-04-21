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
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupPath
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ObbNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val OBB_FILE_NAME = "main.87.com.mages.chaoschild_jp.obb"

private val OkkeiEnvironment.obbPath: Path
	get() = externalStoragePath / "Android" / "obb" / GAME_PACKAGE_NAME / OBB_FILE_NAME

@Singleton
class ObbRepositoryImpl @Inject constructor(
	environment: OkkeiEnvironment,
	private val fileSystem: FileSystem
) : ObbRepository {

	override val obbExists: Boolean
		get() = fileSystem.exists(obbPath)

	override val obbPath = environment.obbPath

	override fun deleteObb() {
		fileSystem.delete(obbPath)
	}
}

@Singleton
class ObbBackupRepositoryImpl @Inject constructor(
	environment: OkkeiEnvironment,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val hashRepository: HashRepository,
	private val fileSystem: FileSystem
) : ObbBackupRepository {

	override val backupExists: Boolean
		get() = fileSystem.exists(backup)

	private val obb = environment.obbPath
	private val backup = environment.backupPath / OBB_FILE_NAME

	override fun deleteBackup() {
		fileSystem.delete(backup)
	}

	override fun createBackup(): ProgressOperation<Unit> {
		val progressMultiplier = 6
		return operation(progressMax = STREAM_COPY_PROGRESS_MAX * progressMultiplier) {
			if (!fileSystem.exists(obb)) {
				throw ObbNotFoundException()
			}
			try {
				val hash = withContext(ioDispatcher) {
					fileSystem.copy(
						obb, backup, hashing = true,
						onProgressDeltaChanged = { progressDelta(it * progressMultiplier) }
					)
				}
				hashRepository.backupObbHash.persist(hash)
			} catch (t: Throwable) {
				fileSystem.delete(backup)
				throw t
			}
		}
	}

	override fun restoreBackup(): ProgressOperation<Unit> {
		val progressMultiplier = 3
		return operation(progressMax = STREAM_COPY_PROGRESS_MAX * progressMultiplier) {
			if (!fileSystem.exists(backup)) {
				throw ObbNotFoundException()
			}
			try {
				withContext(ioDispatcher) {
					fileSystem.copy(backup, obb, onProgressDeltaChanged = { progressDelta(it * progressMultiplier) })
				}
			} catch (t: Throwable) {
				fileSystem.delete(obb)
				throw t
			}
		}
	}

	override fun verifyBackup() = operation(progressMax = STREAM_COPY_PROGRESS_MAX) {
		val savedHash = hashRepository.backupObbHash.retrieve()
		if (savedHash.isEmpty() || !fileSystem.exists(backup)) {
			return@operation false
		}
		val fileHash = withContext(ioDispatcher) {
			fileSystem.computeHash(backup, onProgressDeltaChanged = { progressDelta(it) })
		}
		return@operation fileHash == savedHash
	}
}