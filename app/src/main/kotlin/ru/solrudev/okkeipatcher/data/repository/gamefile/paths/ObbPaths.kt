package ru.solrudev.okkeipatcher.data.repository.gamefile.paths

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath
import ru.solrudev.okkeipatcher.data.util.GAME_PACKAGE_NAME
import java.io.File
import javax.inject.Inject

private const val OBB_FILE_NAME = "main.87.com.mages.chaoschild_jp.obb"

interface ObbPaths {
	val obb: Path
	val backup: Path
}

class ObbPathsImpl @Inject constructor(@ApplicationContext applicationContext: Context) : ObbPaths {

	override val obb = File(
		Environment.getExternalStorageDirectory(),
		"Android/obb/$GAME_PACKAGE_NAME/$OBB_FILE_NAME"
	).toOkioPath()

	override val backup = File(applicationContext.backupDir, OBB_FILE_NAME).toOkioPath()
}