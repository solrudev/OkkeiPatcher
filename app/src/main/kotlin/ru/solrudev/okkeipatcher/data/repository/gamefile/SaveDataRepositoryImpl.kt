package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Result
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import java.io.File
import javax.inject.Inject

class SaveDataRepositoryImpl @Inject constructor(
	@ApplicationContext applicationContext: Context,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val saveDataFile: SaveDataFile,
	private val streamCopier: StreamCopier,
	private val commonFilesHashRepository: CommonFilesHashRepository
) : SaveDataRepository {

	private val backup = File(applicationContext.backupDir, "SAVEDATA.DAT")
	private val temp = File(applicationContext.backupDir, "SAVEDATA_TEMP.DAT")

	override val backupExists: Boolean
		get() = backup.exists()

	override fun deleteBackup() {
		backup.delete()
	}

	override fun deleteTemp() {
		temp.delete()
	}

	override suspend fun createTemp(): Result {
		val failure = Result.Failure(
			LocalizedString.resource(R.string.warning_could_not_backup_save_data)
		)
		if (!saveDataFile.exists) {
			return failure
		}
		temp.recreate()
		try {
			val currentSource = withContext(ioDispatcher) { saveDataFile.inputStream()?.source() } ?: return failure
			currentSource.use { source ->
				val sink = withContext(ioDispatcher) { temp.sink() }
				streamCopier.copy(source, sink, saveDataFile.length)
				return Result.Success
			}
		} catch (t: Throwable) {
			return failure
		}
	}

	override suspend fun verifyBackup(): Boolean {
		val savedHash = commonFilesHashRepository.saveDataHash.retrieve()
		if (savedHash.isEmpty() || !backup.exists()) {
			return false
		}
		val backupHash = streamCopier.computeHash(backup, ioDispatcher)
		return backupHash == savedHash
	}

	override suspend fun restoreBackup(): Result {
		val failure = Result.Failure(
			LocalizedString.resource(R.string.warning_could_not_restore_save_data)
		)
		try {
			saveDataFile.recreate()
			val currentSink = withContext(ioDispatcher) { saveDataFile.outputStream()?.sink() } ?: return failure
			currentSink.use { sink ->
				val source = withContext(ioDispatcher) { backup.source() }
				streamCopier.copy(source, sink, backup.length())
				return Result.Success
			}
		} catch (t: Throwable) {
			return failure
		}
	}

	override suspend fun persistTempAsBackup() {
		if (temp.exists()) {
			backup.delete()
			temp.renameTo(backup)
		}
		if (backup.exists()) {
			val backupHash = streamCopier.computeHash(backup, ioDispatcher)
			commonFilesHashRepository.saveDataHash.persist(backupHash)
		}
	}
}