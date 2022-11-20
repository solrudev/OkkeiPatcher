package ru.solrudev.okkeipatcher.data.repository.gamefile.paths

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath
import java.io.File
import javax.inject.Inject

interface SaveDataPaths {
	val backup: Path
	val temp: Path
}

class SaveDataPathsImpl @Inject constructor(@ApplicationContext applicationContext: Context) : SaveDataPaths {
	override val backup = File(applicationContext.backupDir, "SAVEDATA.DAT").toOkioPath()
	override val temp = File(applicationContext.backupDir, "SAVEDATA_TEMP.DAT").toOkioPath()
}