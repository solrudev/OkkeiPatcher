package ru.solrudev.okkeipatcher.data.repository.gamefile.paths

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath
import ru.solrudev.okkeipatcher.data.util.externalDir
import java.io.File
import javax.inject.Inject

interface ApkPaths {
	val temp: Path
	val backup: Path
}

class ApkPathsImpl @Inject constructor(@ApplicationContext private val applicationContext: Context) : ApkPaths {
	override val temp = File(applicationContext.externalDir, "temp.apk").toOkioPath()
	override val backup = File(applicationContext.backupDir, "backup.apk").toOkioPath()
}