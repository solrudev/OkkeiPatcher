package ru.solrudev.okkeipatcher.data.repository.gamefile

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Sink
import ru.solrudev.okkeipatcher.data.repository.gamefile.paths.ObbPaths
import ru.solrudev.okkeipatcher.data.util.STREAM_COPY_PROGRESS_MAX
import ru.solrudev.okkeipatcher.data.util.computeHash
import ru.solrudev.okkeipatcher.data.util.copy
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ObbNotFoundException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import javax.inject.Inject

class ObbRepositoryImpl @Inject constructor(
	obbPaths: ObbPaths,
	private val fileSystem: FileSystem
) : ObbRepository {

	override val obbExists: Boolean
		get() = fileSystem.exists(obb)

	private val obb = obbPaths.obb

	override fun deleteObb() {
		fileSystem.delete(obb)
	}

	override fun obbSink(): Sink {
		fileSystem.prepareRecreate(obb)
		return fileSystem.sink(obb)
	}
}

class ObbBackupRepositoryImpl @Inject constructor(
	obbPaths: ObbPaths,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val fileSystem: FileSystem
) : ObbBackupRepository {

	override val backupExists: Boolean
		get() = fileSystem.exists(backup)

	private val obb = obbPaths.obb
	private val backup = obbPaths.backup

	override fun deleteBackup() {
		fileSystem.delete(backup)
	}

	override fun createBackup(): ProgressOperation<Unit> {
		val progressMultiplier = 6
		return operation(progressMax = 100 * progressMultiplier) {
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
				commonFilesHashRepository.backupObbHash.persist(hash)
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
		val savedHash = commonFilesHashRepository.backupObbHash.retrieve()
		if (savedHash.isEmpty() || !fileSystem.exists(backup)) {
			return@operation false
		}
		val fileHash = withContext(ioDispatcher) {
			fileSystem.computeHash(backup, onProgressDeltaChanged = { progressDelta(it) })
		}
		return@operation fileHash == savedHash
	}
}