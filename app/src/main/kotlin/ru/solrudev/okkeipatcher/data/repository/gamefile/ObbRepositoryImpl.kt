package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import okio.Sink
import okio.sink
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ObbNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import java.io.File
import javax.inject.Inject

private const val OBB_FILE_NAME = "main.87.com.mages.chaoschild_jp.obb"

private val obbFile = File(
	Environment.getExternalStorageDirectory(),
	"Android/obb/$GAME_PACKAGE_NAME/$OBB_FILE_NAME"
)

class ObbRepositoryImpl @Inject constructor() : ObbRepository {

	override val obbExists: Boolean
		get() = obbFile.exists()

	override fun deleteObb() {
		obbFile.delete()
	}

	override fun obbSink(): Sink {
		obbFile.recreate()
		return obbFile.sink()
	}
}

class ObbBackupRepositoryImpl @Inject constructor(
	private val streamCopier: StreamCopier,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext applicationContext: Context
) : ObbBackupRepository {

	private val backup = File(applicationContext.backupDir, OBB_FILE_NAME)

	override val backupExists: Boolean
		get() = backup.exists()

	override fun deleteBackup() {
		backup.delete()
	}

	override fun createBackup(): ProgressOperation<Unit> {
		val progressMultiplier = 6
		return operation(progressMax = streamCopier.progressMax * progressMultiplier) {
			if (!obbFile.exists()) {
				throw ObbNotFoundException()
			}
			try {
				val hash = streamCopier.copy(obbFile, backup, ioDispatcher, hashing = true) {
					progressDelta(it * progressMultiplier)
				}
				commonFilesHashRepository.backupObbHash.persist(hash)
			} catch (t: Throwable) {
				backup.delete()
				throw t
			}
		}
	}

	override fun restore(): ProgressOperation<Unit> {
		val progressMultiplier = 3
		return operation(progressMax = streamCopier.progressMax * progressMultiplier) {
			if (!backup.exists()) {
				throw ObbNotFoundException()
			}
			try {
				streamCopier.copy(backup, obbFile, ioDispatcher) {
					progressDelta(it * progressMultiplier)
				}
			} catch (t: Throwable) {
				obbFile.delete()
				throw t
			}
		}
	}

	override fun verifyBackup() = operation(progressMax = streamCopier.progressMax) {
		val savedHash = commonFilesHashRepository.backupObbHash.retrieve()
		if (savedHash.isEmpty() || !backup.exists()) {
			return@operation false
		}
		val fileHash = streamCopier.computeHash(backup, ioDispatcher, ::progressDelta)
		fileHash == savedHash
	}
}