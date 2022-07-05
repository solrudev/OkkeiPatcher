package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.service.copy
import ru.solrudev.okkeipatcher.domain.core.operation.ProgressOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.service.util.recreate
import java.io.File
import java.io.OutputStream
import javax.inject.Inject

private const val PACKAGE_NAME = "com.mages.chaoschild_jp"
private const val OBB_FILE_NAME = "main.87.com.mages.chaoschild_jp.obb"

class ObbRepositoryImpl @Inject constructor(
	private val streamCopier: StreamCopier,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	@ApplicationContext applicationContext: Context
) : ObbRepository {

	override val obbExists: Boolean
		get() = obbFile.exists()

	override val backupExists: Boolean
		get() = backup.exists()

	private val obbFile = File(
		Environment.getExternalStorageDirectory(),
		"Android/obb/$PACKAGE_NAME/$OBB_FILE_NAME"
	)

	private val backup = File(applicationContext.backupDir, OBB_FILE_NAME)

	override fun deleteObb() {
		obbFile.delete()
	}

	override fun deleteBackup() {
		backup.delete()
	}

	override fun openObbInputStream() = obbFile.inputStream()

	override fun openObbOutputStream(): OutputStream {
		obbFile.recreate()
		return obbFile.outputStream()
	}

	override fun backup() = operation(
		progressMax = streamCopier.progressMax * 6
	) {
		if (!obbExists) {
			throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
		}
		try {
			val hash = streamCopier.copy(obbFile, backup, hashing = true) {
				progressDelta(it * 6)
			}
			commonFilesHashRepository.backupObbHash.persist(hash)
		} catch (t: Throwable) {
			backup.delete()
			throw t
		}
	}

	override fun restore(): ProgressOperation<Unit> = operation(
		progressMax = streamCopier.progressMax * 3
	) {
		if (!backupExists) {
			throw LocalizedException(LocalizedString.resource(R.string.error_obb_not_found))
		}
		try {
			streamCopier.copy(backup, obbFile) {
				progressDelta(it * 3)
			}
		} catch (t: Throwable) {
			obbFile.delete()
			throw t
		}
	}

	override fun verifyBackup() = operation(
		progressMax = streamCopier.progressMax
	) {
		val savedHash = commonFilesHashRepository.backupObbHash.retrieve()
		if (savedHash.isEmpty() || !backup.exists()) {
			return@operation false
		}
		val fileHash = streamCopier.computeHash(backup, ::progressDelta)
		fileHash == savedHash
	}
}