package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import android.os.Build
import com.aefyr.pseudoapksigner.PseudoApkSigner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.data.service.util.use
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.app.CommonFilesHashRepository
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.inject.Inject

private const val CERTIFICATE_FILE_NAME = "testkey.x509.pem"
private const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"
private const val TEMPLATE_FILE_NAME = "testkey.past"
private const val CREATED_BY = "Okkei Patcher"
private const val SIGNER_NAME = "Okkei"

interface ApkSigner {
	suspend fun sign(apk: File)
	suspend fun removeSignature(apk: File)
}

class ApkSignerImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context,
	private val commonFilesHashRepository: CommonFilesHashRepository,
	private val streamCopier: StreamCopier
) : ApkSigner {

	override suspend fun sign(apk: File) {
		val outputApk = File(apk.parent, "${apk.nameWithoutExtension}-signed.apk")
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			signWithApkSig(apk, outputApk)
		} else {
			signWithPseudoApkSigner(apk, outputApk)
		}
		val outputApkHash = streamCopier.computeHash(outputApk, ioDispatcher)
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
	private suspend inline fun signWithApkSig(apk: File, outputApk: File) {
		val certificate = getSigningCertificate()
		val privateKey = getSigningPrivateKey()
		val signerConfig = com.android.apksig.ApkSigner.SignerConfig.Builder(
			SIGNER_NAME,
			privateKey,
			listOf(certificate)
		).build()
		val apkSigner = com.android.apksig.ApkSigner.Builder(listOf(signerConfig))
			.setCreatedBy(CREATED_BY)
			.setInputApk(apk)
			.setOutputApk(outputApk)
			.build()
		withContext(ioDispatcher) {
			apkSigner.sign()
		}
	}

	private suspend inline fun signWithPseudoApkSigner(apk: File, outputApk: File) {
		val templateFile = getSigningTemplateFile()
		val privateKeyFile = getSigningPrivateKeyFile()
		withContext(ioDispatcher) {
			PseudoApkSigner(templateFile, privateKeyFile)
				.apply { setSignerName(SIGNER_NAME) }
				.sign(apk, outputApk)
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

	private suspend inline fun getSigningPrivateKeyFile() = extractAssetFile(PRIVATE_KEY_FILE_NAME)
	private suspend inline fun getSigningTemplateFile() = extractAssetFile(TEMPLATE_FILE_NAME)

	@Suppress("BlockingMethodInNonBlockingContext")
	private suspend inline fun extractAssetFile(fileName: String) = withContext(ioDispatcher) {
		val file = File(applicationContext.filesDir, fileName)
		if (file.exists()) {
			return@withContext file
		}
		val assets = applicationContext.assets
		assets.openFd(fileName).use { fd ->
			streamCopier.copy(
				fd.createInputStream().source(),
				file.sink(),
				fd.declaredLength
			)
		}
		file
	}
}