package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.repository.gamefile.util.backupDir
import ru.solrudev.okkeipatcher.data.service.StreamCopier
import ru.solrudev.okkeipatcher.data.service.computeHash
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.data.util.recreate
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.SaveDataRepository
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

private const val FILES_DIR_NAME = "files"
private const val CURRENT_SAVE_DATA_NAME = "SAVEDATA.DAT"

private val CURRENT_SAVE_DATA_PATH =
	"${Environment.getExternalStorageDirectory().absolutePath}/Android/data/$GAME_PACKAGE_NAME/$FILES_DIR_NAME"

class SaveDataRepositoryImpl @Inject constructor(
	@ApplicationContext private val applicationContext: Context,
	private val streamCopier: StreamCopier,
	private val commonFilesHashRepository: CommonFilesHashRepository
) : SaveDataRepository {

	private val backup = File(applicationContext.backupDir, "SAVEDATA.DAT")
	private val temp = File(applicationContext.backupDir, "SAVEDATA_TEMP.DAT")

	private val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
		SaveDataDocumentFile(applicationContext)
	} else {
		SaveDataRawFile()
	}

	override val backupExists: Boolean
		get() = backup.exists()

	override fun deleteBackup() {
		backup.delete()
	}

	override fun deleteTemp() {
		temp.delete()
	}

	override suspend fun createTemp(): Boolean {
		if (current.exists) {
			temp.recreate()
			val currentSource = current.inputStream()?.source() ?: return false
			streamCopier.copy(currentSource, temp.sink(), current.length)
			return true
		} else {
			return false
		}
	}

	override suspend fun verifyBackup(): Boolean {
		val savedHash = commonFilesHashRepository.saveDataHash.retrieve()
		if (savedHash.isEmpty() || !backup.exists()) {
			return false
		}
		val backupHash = streamCopier.computeHash(backup)
		return backupHash == savedHash
	}

	override suspend fun restore(): Boolean {
		current.recreate()
		val currentSink = current.outputStream()?.sink() ?: return false
		streamCopier.copy(backup.source(), currentSink, backup.length())
		return true
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
	fun inputStream(): InputStream?
	fun outputStream(): OutputStream?
}

private class SaveDataRawFile : SaveDataFile {

	private val file = File(CURRENT_SAVE_DATA_PATH, CURRENT_SAVE_DATA_NAME)

	override val exists: Boolean
		get() = file.exists()

	override val length: Long
		get() = file.length()

	override fun recreate() = file.recreate()
	override fun inputStream() = file.inputStream()
	override fun outputStream() = file.outputStream()
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private class SaveDataDocumentFile(private val applicationContext: Context) : SaveDataFile {

	private var documentUri = createDocumentFile()?.uri

	override val exists: Boolean
		get() = createDocumentFile()?.exists() ?: false

	override val length: Long
		get() = createDocumentFile()?.length() ?: -1L

	override fun recreate() {
		val file = createDocumentFile()
		if (file?.exists() == true) {
			file.delete()
		} else {
			DocumentFile
				.fromTreeUri(applicationContext, ANDROID_DATA_TREE_URI)
				?.createDirectory(GAME_PACKAGE_NAME)
				?.createDirectory(FILES_DIR_NAME)
		}
		val dirUri = DocumentsContract.buildDocumentUriUsingTree(
			ANDROID_DATA_TREE_URI,
			DocumentsContract.getTreeDocumentId(ANDROID_DATA_TREE_URI) +
					"/$GAME_PACKAGE_NAME/$FILES_DIR_NAME/"
		)
		documentUri = DocumentsContract.createDocument(
			applicationContext.contentResolver,
			dirUri,
			"application/octet-stream",
			CURRENT_SAVE_DATA_NAME
		)
	}

	override fun inputStream() = documentUri?.let {
		applicationContext.contentResolver.openInputStream(it)
	}

	override fun outputStream() = documentUri?.let {
		applicationContext.contentResolver.openOutputStream(it)
	}

	private fun createDocumentFile(): DocumentFile? {
		val fileUri = DocumentsContract.buildDocumentUriUsingTree(
			ANDROID_DATA_TREE_URI,
			DocumentsContract.getTreeDocumentId(ANDROID_DATA_TREE_URI) +
					"/$GAME_PACKAGE_NAME/$FILES_DIR_NAME/$CURRENT_SAVE_DATA_NAME"
		)
		return DocumentFile.fromSingleUri(applicationContext, fileUri)
	}
}