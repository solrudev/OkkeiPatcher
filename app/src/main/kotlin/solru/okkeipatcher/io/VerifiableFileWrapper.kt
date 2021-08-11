package solru.okkeipatcher.io

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import solru.okkeipatcher.io.base.FileWrapper
import solru.okkeipatcher.io.base.Verifiable
import solru.okkeipatcher.io.services.base.IoService
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.empty
import solru.okkeipatcher.utils.extensions.isEmptyOrBlank

abstract class VerifiableFileWrapper(
	private val fileImplementation: FileWrapper,
	ioService: IoService
) : FileWrapper(fileImplementation.fullPath, fileImplementation.fileName, ioService), Verifiable {

	override val exists: Boolean
		get() = fileImplementation.exists

	override val length: Long
		get() = fileImplementation.length

	override fun create() = fileImplementation.create()
	override fun deleteIfExists() = fileImplementation.deleteIfExists()
	override fun renameTo(fileName: String) = fileImplementation.renameTo(fileName)
	override fun createInputStream() = fileImplementation.createInputStream()
	override fun createOutputStream() = fileImplementation.createOutputStream()

	protected suspend fun compareBySharedPreferences(key: String): Boolean {
		var md5 = String.empty
		val md5ToCompare = Preferences.get(key, String.empty)
		if (md5ToCompare.isEmptyOrBlank()) {
			return false
		}
		if (this.exists) {
			md5 = this.computeMd5()
		}
		return md5 == md5ToCompare
	}

	protected suspend fun compareByFile(file: FileWrapper): Boolean {
		var md5 = String.empty
		var md5ToCompare = String.empty
		if (file.exists) {
			coroutineScope {
				val progressJob = launch {
					file.progress.collect {
						progressMutable.emit(it)
					}
				}
				md5ToCompare = file.computeMd5()
				progressJob.cancel()
			}
		}
		if (md5ToCompare.isEmptyOrBlank()) {
			return false
		}
		if (this.exists) {
			md5 = this.computeMd5()
		}
		return md5 == md5ToCompare
	}
}