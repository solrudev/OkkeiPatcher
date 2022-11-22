package ru.solrudev.okkeipatcher.data.repository.gamefile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.*
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.util.ANDROID_DATA_TREE_URI
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import ru.solrudev.okkeipatcher.data.util.prepareRecreate
import javax.inject.Inject

private const val FILES_DIR_NAME = "files"
private const val SAVE_DATA_NAME = "SAVEDATA.DAT"

private val OkkeiEnvironment.saveDataPath: Path
	get() = externalStoragePath / "Android" / "data" / GAME_PACKAGE_NAME / FILES_DIR_NAME / SAVE_DATA_NAME

interface SaveDataFile {
	val exists: Boolean
	val length: Long
	fun recreate()
	fun source(): Source?
	fun sink(): Sink?
}

class SaveDataRawFile @Inject constructor(
	environment: OkkeiEnvironment,
	private val fileSystem: FileSystem
) : SaveDataFile {

	private val path = environment.saveDataPath

	override val exists: Boolean
		get() = fileSystem.exists(path)

	override val length: Long
		get() = fileSystem.metadata(path).size ?: 0

	override fun recreate() = fileSystem.prepareRecreate(path)
	override fun source() = fileSystem.source(path)
	override fun sink() = fileSystem.sink(path)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class SaveDataDocumentFile @Inject constructor(
	@ApplicationContext private val applicationContext: Context
) : SaveDataFile {

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
			DocumentFile.fromTreeUri(applicationContext, ANDROID_DATA_TREE_URI)
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
			SAVE_DATA_NAME
		)
	}

	@SuppressLint("Recycle")
	override fun source() = documentUri?.let {
		applicationContext.contentResolver.openInputStream(it)?.source()
	}

	@SuppressLint("Recycle")
	override fun sink() = documentUri?.let {
		applicationContext.contentResolver.openOutputStream(it)?.sink()
	}

	private fun createDocumentFile(): DocumentFile? {
		val fileUri = DocumentsContract.buildDocumentUriUsingTree(
			ANDROID_DATA_TREE_URI,
			DocumentsContract.getTreeDocumentId(ANDROID_DATA_TREE_URI) +
					"/$GAME_PACKAGE_NAME/$FILES_DIR_NAME/$SAVE_DATA_NAME"
		)
		return DocumentFile.fromSingleUri(applicationContext, fileUri)
	}
}