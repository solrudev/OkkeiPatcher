package ru.solrudev.okkeipatcher.domain.service.gamefile

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.lingala.zip4j.ZipFile
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.service.ApkSigner
import java.io.File
import javax.inject.Inject

class ApkZipPackage @Inject constructor(
	private val apkRepository: ApkRepository,
	private val apkSigner: ApkSigner
) : ZipPackage {

	private val tempZipFiles = mutableListOf<ZipFile>()
	private val tempZipFilesMutex = Mutex()

	override fun close() = runBlocking {
		tempZipFilesMutex.withLock {
			tempZipFiles.forEach {
				it.executorService?.shutdownNow()
				it.close()
			}
			tempZipFiles.clear()
		}
	}

	/**
	 * Creates temporary copy of game APK if it doesn't exist.
	 *
	 * @return temp copy of game APK represented as [ZipFile].
	 */
	override suspend fun toZipFile(): ZipFile {
		if (!apkRepository.tempApk.exists) {
			apkRepository.tempApk.create()
		}
		return ZipFile(apkRepository.tempApk.path)
			.also { zipFile ->
				tempZipFilesMutex.withLock {
					tempZipFiles.add(zipFile)
				}
			}
	}

	override suspend fun sign() {
		val apk = File(apkRepository.tempApk.path)
		apkSigner.sign(apk)
	}

	override suspend fun removeSignature() {
		val apk = File(apkRepository.tempApk.path)
		apkSigner.removeSignature(apk)
	}
}