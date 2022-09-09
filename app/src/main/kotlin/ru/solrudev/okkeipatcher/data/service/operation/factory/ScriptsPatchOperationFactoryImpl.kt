package ru.solrudev.okkeipatcher.data.service.operation.factory

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.data.service.factory.ApkZipPackageFactory
import ru.solrudev.okkeipatcher.data.service.operation.ScriptsPatchOperation
import ru.solrudev.okkeipatcher.data.util.externalDir
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ScriptsPatchOperationFactory
import javax.inject.Inject

class ScriptsPatchOperationFactoryImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	@ApplicationContext private val applicationContext: Context,
	private val fileDownloader: FileDownloader,
	private val apkZipPackageFactory: ApkZipPackageFactory
) : ScriptsPatchOperationFactory {

	override fun create(scriptsPatchFile: PatchFile) = ScriptsPatchOperation(
		scriptsPatchFile,
		apkZipPackageFactory,
		ioDispatcher,
		applicationContext.externalDir,
		fileDownloader
	)
}