package ru.solrudev.okkeipatcher.data.service

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import java.io.File

interface ZipPackage : AutoCloseable {
	suspend fun addFiles(files: List<File>, root: String)
	suspend fun removeFiles(files: List<String>)
	suspend fun sign()
	suspend fun removeSignature()
}

class ApkZipPackage(
	private val apkRepository: ApkRepository,
	private val apkSigner: ApkSigner,
	private val ioDispatcher: CoroutineDispatcher
) : ZipPackage {

	private val zipFile = ZipFile(apkRepository.tempPath)

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
		val apk = File(apkRepository.tempPath)
		apkSigner.sign(apk)
	}

	override suspend fun removeSignature() {
		val apk = File(apkRepository.tempPath)
		apkSigner.removeSignature(apk)
	}

	suspend fun create() {
		if (!apkRepository.tempExists) {
			apkRepository.createTemp()
		}
	}
}