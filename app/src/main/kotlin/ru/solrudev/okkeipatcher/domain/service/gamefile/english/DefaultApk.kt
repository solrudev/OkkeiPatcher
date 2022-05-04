package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.IoDispatcher
import ru.solrudev.okkeipatcher.domain.file.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.operation.AggregateOperation
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

	override fun patch() = object : AbstractOperation<Unit>() {

		private val installPatchedOperation = installPatched(updating = false)

		override val status = withStatusFlows(
			scriptsPatchOperation.status,
			installPatchedOperation.status
		)

		override val progressDelta = withProgressDeltaFlows(
			scriptsPatchOperation.progressDelta,
			installPatchedOperation.progressDelta,
		)

		override val progressMax = installPatchedOperation.progressMax + scriptsPatchOperation.progressMax

		override suspend fun invoke() {
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (commonFiles.signedApk.verify().invoke()) {
				progressDelta(scriptsPatchOperation.progressMax)
				installPatchedOperation()
				return
			}
			scriptsPatchOperation()
			installPatchedOperation()
		}
	}

	override fun update() = object : AggregateOperation(
		listOf(
			scriptsPatchOperation,
			installPatched(updating = true)
		)
	) {
		override suspend fun doBefore() {
			commonFiles.tempApk.delete()
			commonFiles.signedApk.delete()
		}
	}
}