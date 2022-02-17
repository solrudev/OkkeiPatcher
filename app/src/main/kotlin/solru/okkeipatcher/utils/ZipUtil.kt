package solru.okkeipatcher.utils

import java.io.File

private val tempZipFilesRegex = Regex("(apk|zip)\\d+")

fun deleteTempZipFiles(directory: File?) {
	if (directory == null || !directory.isDirectory) return
	directory.listFiles()
		?.filter { it.extension.matches(tempZipFilesRegex) }
		?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
}