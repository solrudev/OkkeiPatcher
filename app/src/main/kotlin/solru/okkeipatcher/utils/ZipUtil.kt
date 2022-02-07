package solru.okkeipatcher.utils

import java.io.File

fun deleteTempZipFiles(directory: File?) {
	if (directory == null || !directory.isDirectory) return
	directory.listFiles()
		?.filter { it.extension.matches(Regex("(apk|zip)\\d+")) }
		?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
}