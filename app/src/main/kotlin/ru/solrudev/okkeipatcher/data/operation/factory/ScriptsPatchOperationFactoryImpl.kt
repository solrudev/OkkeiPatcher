package ru.solrudev.okkeipatcher.data.operation.factory

import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import ru.solrudev.okkeipatcher.data.OkkeiEnvironment
import ru.solrudev.okkeipatcher.data.operation.ScriptsPatchOperation
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.operation.factory.ScriptsPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import javax.inject.Inject

class ScriptsPatchOperationFactoryImpl @Inject constructor(
	@IoDispatcher private val ioDispatcher: CoroutineDispatcher,
	private val environment: OkkeiEnvironment,
	private val apkRepository: ApkRepository,
	private val fileDownloader: FileDownloader,
	private val fileSystem: FileSystem
) : ScriptsPatchOperationFactory {

	override fun create(scriptsPatchFile: PatchFile) = ScriptsPatchOperation(
		scriptsPatchFile,
		apkRepository,
		ioDispatcher,
		environment.externalFilesPath,
		fileDownloader,
		fileSystem
	)
}