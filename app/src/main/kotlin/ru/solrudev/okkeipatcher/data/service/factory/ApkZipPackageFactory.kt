package ru.solrudev.okkeipatcher.data.service.factory

import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.data.service.ApkSigner
import ru.solrudev.okkeipatcher.data.service.ApkZipPackage
import ru.solrudev.okkeipatcher.data.service.ZipPackage
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import javax.inject.Inject

interface ApkZipPackageFactory : SuspendFactory<ZipPackage>

class ApkZipPackageFactoryImpl @Inject constructor(
	private val apkRepository: ApkRepository,
	private val apkSigner: ApkSigner,
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ApkZipPackageFactory {

	override suspend fun create() = ApkZipPackage(
		apkRepository,
		apkSigner,
		ioDispatcher
	).also { apkZipPackage ->
		try {
			apkZipPackage.create()
		} catch (t: Throwable) {
			apkZipPackage.close()
			throw t
		}
	}
}