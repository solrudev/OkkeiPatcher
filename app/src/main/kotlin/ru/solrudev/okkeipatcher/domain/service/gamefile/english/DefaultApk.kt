package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.PackageInstallerFacade
import ru.solrudev.okkeipatcher.domain.service.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ScriptsPatchOperationFactory
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatchOperationFactory: ScriptsPatchOperationFactory,
	commonFiles: CommonFiles,
	streamCopier: StreamCopier,
	@IoDispatcher ioDispatcher: CoroutineDispatcher,
	@ApplicationContext applicationContext: Context,
	packageInstaller: PackageInstallerFacade
) : Apk(commonFiles, streamCopier, ioDispatcher, applicationContext, packageInstaller) {

	private val scriptsPatchOperation = scriptsPatchOperationFactory.create(this, patchRepository)

	override fun patch(): Operation<Unit> {
		val installPatchedOperation = installPatched(updating = false)
		return operation(scriptsPatchOperation, installPatchedOperation) {
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (commonFiles.signedApk.verify().invoke()) {
				progressDelta(scriptsPatchOperation.progressMax)
				installPatchedOperation()
				return@operation
			}
			scriptsPatchOperation()
			installPatchedOperation()
		}
	}

	override fun update() = aggregateOperation(
		scriptsPatchOperation,
		installPatched(updating = true),
		operation {
			commonFiles.tempApk.delete()
			commonFiles.signedApk.delete()
		}
	)
}