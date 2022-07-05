package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.os.Build
import android.os.Environment
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.makeFile
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import ru.solrudev.okkeipatcher.domain.service.util.recreate
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

private val CURRENT_SAVE_DATA_PATH =
	"${Environment.getExternalStorageDirectory().absolutePath}/Android/data/com.mages.chaoschild_jp/files"

private const val CURRENT_SAVE_DATA_NAME = "SAVEDATA.DAT"

class SaveDataRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val streamCopier: StreamCopier,
	private val commonFilesHashRepository: CommonFilesHashRepository
) : SaveDataRepository {

	private val backup = File(applicationContext.backupDir, "SAVEDATA.DAT")
	private val temp = File(applicationContext.backupDir, "SAVEDATA_TEMP.DAT")

	private val current by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			AndroidFile(CURRENT_SAVE_DATA_PATH, CURRENT_SAVE_DATA_NAME, applicationContext)
		} else {
			RawFile(CURRENT_SAVE_DATA_PATH, CURRENT_SAVE_DATA_NAME)
		}
	}

	override val backupExists: Boolean
		get() = backup.exists()

	override fun deleteBackup() {
		backup.delete()
	}

	override fun deleteTemp() {
		temp.delete()
	}

	override suspend fun createTemp() = if (current.exists) {
		temp.recreate()
		streamCopier.copy(current.inputStream(), temp.outputStream(), current.length)
		true
	} else {
		false
	}

	override suspend fun verifyBackup(): Boolean {
		val savedHash = commonFilesHashRepository.saveDataHash.retrieve()
		if (savedHash.isEmpty() || !backup.exists()) {
			return false
		}
		val backupHash = streamCopier.computeHash(backup)
		return backupHash == savedHash
	}

	override suspend fun restore() {
		current.recreate()
		streamCopier.copy(backup.inputStream(), current.outputStream(), backup.length())
	}

	override suspend fun persistTempAsBackup() {
		if (temp.exists()) {
			backup.delete()
			temp.renameTo(backup)
		}
		if (backup.exists()) {
			val backupHash = streamCopier.computeHash(backup)
			commonFilesHashRepository.saveDataHash.persist(backupHash)
		}
	}
}

private interface SaveDataFile {
	val exists: Boolean
	val length: Long
	fun recreate()
	fun inputStream(): InputStream
	fun outputStream(): OutputStream
}

private class RawFile(path: String, name: String) : SaveDataFile {

	private val file = File(path, name)

	override val exists: Boolean
		get() = file.exists()

	override val length: Long
		get() = file.length()

	override fun recreate() = file.recreate()
	override fun inputStream() = file.inputStream()
	override fun outputStream() = file.outputStream()
}

private class AndroidFile(
	private val path: String,
	private val name: String,
	private val applicationContext: Context
) : SaveDataFile {

	private val documentFile = DocumentFileCompat.fromFullPath(
		applicationContext,
		"$path/$name"
	)

	override val exists: Boolean
		get() = documentFile?.exists() ?: false

	override val length: Long
		get() = documentFile?.length() ?: -1L

	override fun recreate() {
		documentFile?.delete()
		DocumentFileCompat.mkdirs(applicationContext, path)?.makeFile(applicationContext, name)
	}

	override fun inputStream() = documentFile?.openInputStream(applicationContext)!!
	override fun outputStream() = documentFile?.openOutputStream(applicationContext)!!
}