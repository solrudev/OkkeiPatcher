package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import ru.solrudev.okkeipatcher.domain.service.ApkSigner
import ru.solrudev.okkeipatcher.domain.service.util.use
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

private const val CERTIFICATE_FILE_NAME = "testkey.x509.pem"
private const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"

class ApkSignerImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val streamCopier: StreamCopier
) : ApkSigner {

	@Suppress("BlockingMethodInNonBlockingContext")
	override suspend fun sign(apk: File) {
		val certificate = getSigningCertificate()
		val privateKey = getSigningPrivateKey()
		val signerConfig = com.android.apksig.ApkSigner.SignerConfig.Builder(
			"Okkei",
			privateKey,
			listOf(certificate)
		).build()
		val outputApk = File(apk.parent, "${apk.nameWithoutExtension}-signed.apk")
		val apkSigner = com.android.apksig.ApkSigner.Builder(listOf(signerConfig))
			.setCreatedBy("Okkei Patcher")
			.setInputApk(apk)
			.setOutputApk(outputApk)
			.build()
		withContext(ioDispatcher) {
			apkSigner.sign()
		}
		val outputApkHash = streamCopier.computeHash(outputApk)
		commonFilesHashRepository.signedApkHash.persist(outputApkHash)
		apk.delete()
		outputApk.renameTo(apk)
	}

	override suspend fun removeSignature(apk: File) = withContext(ioDispatcher) {
		ZipFile(apk).use { zipFile ->
			zipFile.removeFile("META-INF/")
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun getSigningCertificate() = withContext(ioDispatcher) {
		val assets = applicationContext.assets
		assets.open(CERTIFICATE_FILE_NAME).use { certificateStream ->
			val certificateFactory = CertificateFactory.getInstance("X.509")
			certificateFactory.generateCertificate(certificateStream) as X509Certificate
		}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun getSigningPrivateKey() = withContext(ioDispatcher) {
		val assets = applicationContext.assets
		assets.openFd(PRIVATE_KEY_FILE_NAME).use { keyFd ->
			val keyByteArray = ByteArray(keyFd.declaredLength.toInt())
			keyFd.createInputStream().use {
				it.read(keyByteArray)
			}
			val keySpec = PKCS8EncodedKeySpec(keyByteArray)
			val keyFactory = KeyFactory.getInstance("RSA")
			keyFactory.generatePrivate(keySpec)
		}
	}
}