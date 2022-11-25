package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import okio.Path
import ru.solrudev.okkeipatcher.domain.service.ZipPackage

class ApkZipPackage(
	private val apkPath: Path,
	private val apkSigner: ApkSigner,
	private val ioDispatcher: CoroutineDispatcher
) : ZipPackage {

	private val zipFile = ZipFile(apkPath.toString())

	override fun close() {
		zipFile.executorService?.shutdownNow()
		zipFile.close()
	}

	override suspend fun addFiles(files: List<Path>, root: String) = runInterruptible(ioDispatcher) {
		val parameters = ZipParameters().apply { rootFolderNameInZip = root }
		zipFile.addFiles(files.map { it.toFile() }, parameters)
	}

	override suspend fun removeFiles(files: List<String>) = runInterruptible(ioDispatcher) {
		zipFile.removeFiles(files)
	}

	override suspend fun sign() {
		apkSigner.sign(apkPath.toFile())
	}

	override suspend fun removeSignature() {
		apkSigner.removeSignature(apkPath.toFile())
	}
}