package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import java.io.File

interface ZipPackage : AutoCloseable {
	suspend fun addFiles(files: List<File>, root: String)
	suspend fun removeFiles(files: List<String>)
	suspend fun sign()
	suspend fun removeSignature()
}

class ApkZipPackage(
	private val apkPath: String,
	private val apkSigner: ApkSigner,
	private val ioDispatcher: CoroutineDispatcher
) : ZipPackage {

	private val zipFile = ZipFile(apkPath)

	override fun close() {
		zipFile.executorService?.shutdownNow()
		zipFile.close()
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun addFiles(files: List<File>, root: String) = withContext(ioDispatcher) {
		val parameters = ZipParameters().apply { rootFolderNameInZip = root }
		zipFile.addFiles(files, parameters)
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun removeFiles(files: List<String>) = withContext(ioDispatcher) {
		zipFile.removeFiles(files)
	}

	override suspend fun sign() {
		val apk = File(apkPath)
		apkSigner.sign(apk)
	}

	override suspend fun removeSignature() {
		val apk = File(apkPath)
		apkSigner.removeSignature(apk)
	}
}