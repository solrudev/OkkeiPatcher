package ru.solrudev.okkeipatcher.domain.service.gamefile

import android.content.Context
import com.android.apksig.ApkSigner
import io.github.solrudev.simpleinstaller.data.InstallResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.lingala.zip4j.ZipFile
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.file.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.service.PackageInstallerFacade
import ru.solrudev.okkeipatcher.domain.util.extension.isPackageInstalled
import ru.solrudev.okkeipatcher.domain.util.extension.use
import ru.solrudev.okkeipatcher.io.file.JavaFile
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import ru.solrudev.okkeipatcher.util.Preferences
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

private const val CERTIFICATE_FILE_NAME = "testkey.x509.pem"
private const val PRIVATE_KEY_FILE_NAME = "testkey.pk8"
private const val PACKAGE_NAME = "com.mages.chaoschild_jp"

abstract class Apk(
	protected val commonFiles: CommonFiles,
	protected val streamCopier: StreamCopier,
	protected val ioDispatcher: CoroutineDispatcher,
	private val applicationContext: Context,
	private val packageInstaller: PackageInstallerFacade
) : PatchableGameFile {

	override val backupExists: Boolean
		get() = commonFiles.backupApk.exists

	private val tempZipFiles = mutableListOf<ZipFile>()
	private val tempZipFilesMutex = Mutex()

	override fun canPatch(onNegative: (LocalizedString) -> Unit): Boolean {
		val canInstallPatchedApk = backupExists && commonFiles.signedApk.exists
		if (!isInstalled() && canInstallPatchedApk) {
			return true
		}
		return if (isInstalled()) {
			true
		} else {
			onNegative(LocalizedString.resource(R.string.error_game_not_found))
			false
		}
	}

	override fun deleteBackup() = commonFiles.backupApk.delete()

	override fun backup() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_comparing_apk))
		if (commonFiles.backupApk.verify().invoke()) {
			return@operation
		}
		if (!isInstalled()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
		}
		try {
			status(LocalizedString.resource(R.string.status_backing_up_apk))
			val hash = copyInstalledApkTo(commonFiles.backupApk, hashing = true)
			status(LocalizedString.resource(R.string.status_writing_apk_hash))
			Preferences.set(CommonFileHashKey.backup_apk_hash.name, hash)
		} catch (t: Throwable) {
			commonFiles.backupApk.delete()
			throw t
		}
	}

	override fun restore(): Operation<Unit> {
		val uninstallOperation = uninstall(updating = false)
		val installBackupOperation = install {
			if (!commonFiles.backupApk.verify().invoke()) {
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			File(commonFiles.backupApk.fullPath)
		}
		return aggregateOperation(uninstallOperation, installBackupOperation)
	}

	/**
	 * Closes all [ZipFile] instances gotten from [toZipFile] and deletes temporary files.
	 */
	override fun close() {
		commonFiles.tempApk.delete()
		runBlocking {
			tempZipFilesMutex.withLock {
				tempZipFiles.forEach {
					it.executorService?.shutdownNow()
					it.close()
				}
				tempZipFiles.clear()
			}
		}
	}

	/**
	 * Creates temporary copy of game APK if it doesn't exist.
	 *
	 * @return temp copy of game APK represented as [ZipFile].
	 */
	suspend fun toZipFile(): ZipFile {
		if (!commonFiles.tempApk.exists) {
			copyInstalledApkTo(commonFiles.tempApk)
		}
		return ZipFile(commonFiles.tempApk.fullPath)
			.also { zipFile ->
				tempZipFilesMutex.withLock {
					tempZipFiles.add(zipFile)
				}
			}
	}

	@Suppress("BlockingMethodInNonBlockingContext")
	fun sign() = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_signing_apk))
		val certificate = getSigningCertificate()
		val privateKey = getSigningPrivateKey()
		val signerConfig = ApkSigner.SignerConfig.Builder(
			"Okkei",
			privateKey,
			listOf(certificate)
		).build()
		val inputApk = File(commonFiles.tempApk.fullPath)
		val outputApk = File(commonFiles.signedApk.fullPath)
		val apkSigner = ApkSigner.Builder(listOf(signerConfig))
			.setCreatedBy("Okkei Patcher")
			.setInputApk(inputApk)
			.setOutputApk(outputApk)
			.build()
		withContext(ioDispatcher) {
			apkSigner.sign()
		}
		status(LocalizedString.resource(R.string.status_writing_patched_apk_hash))
		Preferences.set(
			CommonFileHashKey.signed_apk_hash.name,
			commonFiles.signedApk.computeHash().invoke()
		)
	}

	fun removeSignature() = operation(progressMax = 100) {
		toZipFile().use { zipFile ->
			status(LocalizedString.resource(R.string.status_removing_signature))
			withContext(ioDispatcher) {
				zipFile.removeFile("META-INF/")
			}
		}
	}

	protected fun installPatched(updating: Boolean): Operation<Unit> {
		val uninstallOperation = uninstall(updating)
		val installOperation = install { File(commonFiles.signedApk.fullPath) }
		return operation(uninstallOperation, installOperation) {
			if (!commonFiles.signedApk.exists) {
				throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
			}
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (!commonFiles.signedApk.verify().invoke()) {
				commonFiles.signedApk.delete()
				throw LocalizedException(LocalizedString.resource(R.string.error_not_trustworthy_apk))
			}
			uninstallOperation()
			installOperation()
			commonFiles.signedApk.delete()
		}
	}

	private fun uninstall(updating: Boolean) = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_uninstalling))
		if (updating || !isInstalled()) {
			return@operation
		}
		val uninstallResult = packageInstaller.uninstallPackage(PACKAGE_NAME)
		if (!uninstallResult) {
			throw LocalizedException(LocalizedString.resource(R.string.error_uninstall))
		}
	}

	private fun install(apkFactory: suspend () -> File) = operation(progressMax = 100) {
		status(LocalizedString.resource(R.string.status_installing))
		val apk = apkFactory()
		if (!apk.exists()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_apk_not_found))
		}
		val installResult = packageInstaller.installPackage(apk)
		if (installResult is InstallResult.Failure) {
			throw LocalizedException(
				LocalizedString.resource(
					R.string.error_install,
					installResult.cause?.message.toString()
				)
			)
		}
	}

	/**
	 * @param hashing Does output stream need to be hashed. Default is `false`.
	 * @return File hash. Empty string if [hashing] is `false`.
	 */
	private suspend fun copyInstalledApkTo(
		destinationFile: ru.solrudev.okkeipatcher.io.file.File,
		hashing: Boolean = false
	) = coroutineScope {
		if (!isInstalled()) {
			throw LocalizedException(LocalizedString.resource(R.string.error_game_not_found))
		}
		val installedApkPath = applicationContext.packageManager
			.getPackageInfo(PACKAGE_NAME, 0)
			.applicationInfo
			.publicSourceDir
		val installedApk = JavaFile(File(installedApkPath), streamCopier)
		installedApk.copyTo(destinationFile, hashing).invoke()
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

	private fun isInstalled() = applicationContext.isPackageInstalled(PACKAGE_NAME)
}