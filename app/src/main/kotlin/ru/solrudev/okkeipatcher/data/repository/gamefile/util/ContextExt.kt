package ru.solrudev.okkeipatcher.data.repository.gamefile.util

import android.content.Context
import ru.solrudev.okkeipatcher.data.util.externalDir
import java.io.File

val Context.backupDir: File
	get() = File(externalDir, "backup")