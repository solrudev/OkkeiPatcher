package ru.solrudev.okkeipatcher.data.service

import android.content.Context
import android.os.Build
import com.aefyr.pseudoapksigner.PseudoApkSigner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import net.lingala.zip4j.ZipFile
import okio.buffer
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.data.service.util.computeHash
import ru.solrudev.okkeipatcher.data.service.util.use
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.HashRepository
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
	private val hashRepository: HashRepository
) : ApkSigner {

	override suspend fun sign(apk: File) {
		val outputApk = File(apk.parent, "${apk.nameWithoutExtension}-signed.apk")
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				signWithApkSig(apk, outputApk)
			} else {
				signWithPseudoApkSigner(apk, outputApk)
			}
			val outputApkHash = runInterruptible(ioDispatcher) {
				outputApk.computeHash()
			}
			hashRepository.signedApkHash.persist(outputApkHash)
			apk.delete()
			outputApk.renameTo(apk)
		} catch (t: Throwable) {
			outputApk.delete()
			throw t
		}
	}

	override suspend fun removeSignature(apk: File) = runInterruptible(ioDispatcher) {
		ZipFile(apk).use { zipFile ->
			val headers = zipFile.fileHeaders
				.filter { it.fileName.startsWith("META-INF/") }
				.map { it.fileName }
			zipFile.removeFiles(headers)
		}
	}

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
		runInterruptible(ioDispatcher) {
			apkSigner.sign()
		}
	}

	private suspend inline fun signWithPseudoApkSigner(apk: File, outputApk: File) {
		val templateFile = getSigningTemplateFile()
		val privateKeyFile = getSigningPrivateKeyFile()
		runInterruptible(ioDispatcher) {
			PseudoApkSigner(templateFile, privateKeyFile)
				.apply { setSignerName(SIGNER_NAME) }
				.sign(apk, outputApk)
		}
	}

	private suspend inline fun getSigningCertificate() = runInterruptible(ioDispatcher) {
		val assets = applicationContext.assets
		assets.open(CERTIFICATE_FILE_NAME).use { certificateStream ->
			val certificateFactory = CertificateFactory.getInstance("X.509")
			return@runInterruptible certificateFactory.generateCertificate(certificateStream) as X509Certificate
		}
	}

	private suspend inline fun getSigningPrivateKey() = runInterruptible(ioDispatcher) {
		val assets = applicationContext.assets
		assets.openFd(PRIVATE_KEY_FILE_NAME).use { keyFd ->
			val keyByteArray = ByteArray(keyFd.declaredLength.toInt())
			keyFd.createInputStream().use {
				it.read(keyByteArray)
			}
			val keySpec = PKCS8EncodedKeySpec(keyByteArray)
			val keyFactory = KeyFactory.getInstance("RSA")
			return@runInterruptible keyFactory.generatePrivate(keySpec)
		}
	}

	private suspend inline fun getSigningPrivateKeyFile() = extractAssetFile(PRIVATE_KEY_FILE_NAME)
	private suspend inline fun getSigningTemplateFile() = extractAssetFile(TEMPLATE_FILE_NAME)

	private suspend inline fun extractAssetFile(fileName: String) = runInterruptible(ioDispatcher) {
		val file = File(applicationContext.filesDir, fileName)
		if (file.exists()) {
			return@runInterruptible file
		}
		applicationContext.assets.open(fileName).source().use { source ->
			file.sink().buffer().use { sink ->
				sink.writeAll(source)
			}
		}
		return@runInterruptible file
	}
}