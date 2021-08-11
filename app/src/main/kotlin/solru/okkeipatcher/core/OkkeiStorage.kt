package solru.okkeipatcher.core

import android.os.Environment
import solru.okkeipatcher.MainApplication
import java.io.File

object OkkeiStorage {

	private const val TWO_GB: Long = 2_147_483_648

	val external: File =
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
			&& MainApplication.context.getExternalFilesDir(null) != null
		) {
			MainApplication.context.getExternalFilesDir(null)!!
		} else MainApplication.context.filesDir

	val backup = File(external, "backup")

	val private: File = MainApplication.context.filesDir

	val isEnoughSpace: Boolean
		get() = external.usableSpace >= TWO_GB
}