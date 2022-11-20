package ru.solrudev.okkeipatcher.data.service.factory

import kotlinx.coroutines.CoroutineDispatcher
import okio.Path
import ru.solrudev.okkeipatcher.data.service.ApkSigner
import ru.solrudev.okkeipatcher.data.service.ApkZipPackage
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.service.ZipPackage
import javax.inject.Inject

interface ApkZipPackageFactory {
	fun create(tempPath: Path): ZipPackage
}

class ApkZipPackageFactoryImpl @Inject constructor(
	private val apkSigner: ApkSigner,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ApkZipPackageFactory {

	override fun create(tempPath: Path): ZipPackage {
		return ApkZipPackage(tempPath, apkSigner, ioDispatcher)
	}
}