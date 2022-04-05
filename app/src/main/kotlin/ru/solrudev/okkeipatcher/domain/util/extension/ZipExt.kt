package ru.solrudev.okkeipatcher.domain.util.extension

import net.lingala.zip4j.ZipFile

/**
 * Closes [ZipFile] after executing the given [block] and calls `executorService.shutdownNow()` on exception.
 */
inline fun <T : ZipFile, R> T.use(block: (T) -> R): R {
	var exception: Throwable? = null
	try {
		return block(this)
	} catch (t: Throwable) {
		exception = t
		executorService?.shutdownNow()
		throw t
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