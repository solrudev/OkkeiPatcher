/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.solrudev.okkeipatcher.data.service.apksigner

import android.content.Context
import com.aefyr.pseudoapksigner.PseudoApkSigner
import com.android.apksig.KeyConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runInterruptible
import okio.Path
import okio.buffer
import okio.sink
import okio.source
import ru.solrudev.okkeipatcher.di.IoDispatcher
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

interface ApkSignerImplementation {
	suspend fun sign(apk: Path, outputApk: Path)
}

class ApkSignerApi24 @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context
) : ApkSignerImplementation {

	override suspend fun sign(apk: Path, outputApk: Path) {
		val certificate = getSigningCertificate()
		val privateKey = getSigningPrivateKey()
		val signerConfig = com.android.apksig.ApkSigner.SignerConfig.Builder(
			SIGNER_NAME,
			KeyConfig.Jca(privateKey),
			listOf(certificate)
		).build()
		val apkSigner = com.android.apksig.ApkSigner.Builder(listOf(signerConfig))
			.setCreatedBy(CREATED_BY)
			.setInputApk(apk.toFile())
			.setOutputApk(outputApk.toFile())
			.build()
		runInterruptible(ioDispatcher) {
			apkSigner.sign()
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
}

class ApkSignerApi19 @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context
) : ApkSignerImplementation {

	override suspend fun sign(apk: Path, outputApk: Path) {
		val templateFile = getSigningTemplateFile()
		val privateKeyFile = getSigningPrivateKeyFile()
		runInterruptible(ioDispatcher) {
			PseudoApkSigner(templateFile, privateKeyFile)
				.apply { setSignerName(SIGNER_NAME) }
				.sign(apk.toFile(), outputApk.toFile())
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