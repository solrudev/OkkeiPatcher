package ru.solrudev.okkeipatcher.data.repository.gamefile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.buffer
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupPath
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import javax.inject.Inject

class SaveDataRepositoryImpl @Inject constructor(
	environment: OkkeiEnvironment,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val saveDataFile: SaveDataFile,
	private val hashRepository: HashRepository,
	private val fileSystem: FileSystem
) : SaveDataRepository {

	override val backupExists: Boolean
		get() = fileSystem.exists(backup)

	private val backup = environment.backupPath / "SAVEDATA.DAT"
	private val temp = environment.backupPath / "SAVEDATA_TEMP.DAT"

	override fun deleteBackup() {
		fileSystem.delete(backup)
	}

	override fun deleteTemp() {
		fileSystem.delete(temp)
	}

	override suspend fun createTemp(): Result {
		val failure = Result.failure(R.string.warning_could_not_backup_save_data)
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

	override suspend fun restoreBackup(): Result {
		val failure = Result.failure(R.string.warning_could_not_restore_save_data)
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