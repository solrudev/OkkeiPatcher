package solru.okkeipatcher.domain

import android.os.Environment
import solru.okkeipatcher.OkkeiApplication
import java.io.File

object OkkeiStorage {

	private const val TWO_GB: Long = 2_147_483_648

	val external: File =
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
			&& OkkeiApplication.context.getExternalFilesDir(null) != null
		) {
			OkkeiApplication.context.getExternalFilesDir(null)!!
		} else OkkeiApplication.context.filesDir

	val backup = File(external, "backup")

	val private: File = OkkeiApplication.context.filesDir

	val isEnoughSpace: Boolean
		get() = external.usableSpace >= TWO_GB
}