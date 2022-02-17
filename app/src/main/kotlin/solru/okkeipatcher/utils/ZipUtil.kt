package solru.okkeipatcher.utils

import java.io.File

fun deleteTempZipFiles(directory: File?) {
	if (directory == null || !directory.isDirectory) return
	val regex = Regex("(apk|zip)\\d+")
	directory.listFiles()
		?.filter { it.extension.matches(regex) }
		?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
}