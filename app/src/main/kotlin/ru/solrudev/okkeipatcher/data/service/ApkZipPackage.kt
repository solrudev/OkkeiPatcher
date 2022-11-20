package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import okio.Path

interface ZipPackage : AutoCloseable {
	suspend fun addFiles(files: List<Path>, root: String)
	suspend fun removeFiles(files: List<String>)
	suspend fun sign()
	suspend fun removeSignature()
}

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

	override suspend fun addFiles(files: List<Path>, root: String) = withContext(ioDispatcher) {
		val parameters = ZipParameters().apply { rootFolderNameInZip = root }
		zipFile.addFiles(files.map { it.toFile() }, parameters)
	}

	override suspend fun removeFiles(files: List<String>) = withContext(ioDispatcher) {
		zipFile.removeFiles(files)
	}

	override suspend fun sign() {
		apkSigner.sign(apkPath.toFile())
	}

	override suspend fun removeSignature() {
		apkSigner.removeSignature(apkPath.toFile())
	}
}