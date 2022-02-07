package solru.okkeipatcher.utils

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import solru.okkeipatcher.data.ProgressData
import java.io.File

fun deleteTempZipFiles(directory: File?) {
	if (directory == null || !directory.isDirectory) return
	directory.listFiles()
		?.filter { it.extension.matches(Regex("(apk|zip)\\d+")) }
		?.forEach { if (it.parentFile?.canWrite() == true) it.delete() }
}

/**
 * Closes [ZipFile] after executing the given [block] and calls `executorService.shutdownNow()` on exception.
 */
inline fun <T : ZipFile, R> T.use(block: (T) -> R): R {
	var exception: Throwable? = null
	try {
		return block(this)
	} catch (e: Throwable) {
		exception = e
		executorService?.shutdownNow()
		throw e
	} finally {
		when (exception) {
			null -> close()
			else -> try {
				close()
			} catch (_: Throwable) {
			}
		}
	}
}

suspend inline fun ProgressMonitor.observe(crossinline block: suspend (ProgressData) -> Unit) = coroutineScope {
	while (state == ProgressMonitor.State.BUSY) {
		ensureActive()
		block(ProgressData(workCompleted.toInt(), totalWork.toInt()))
		delay(20)
	}
}