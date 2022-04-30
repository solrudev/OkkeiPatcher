package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.di.factory.ScriptsPatchOperationFactory
import ru.solrudev.okkeipatcher.di.module.IoDispatcher
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.file.english.DefaultPatchFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.operation.AggregateOperation
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.AbstractApk
import ru.solrudev.okkeipatcher.io.service.StreamCopier
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatchOperationFactory: ScriptsPatchOperationFactory,
	patchFiles: DefaultPatchFiles,
	commonFiles: CommonFiles,
	streamCopier: StreamCopier,
	@IoDispatcher ioDispatcher: CoroutineDispatcher,
	@ApplicationContext applicationContext: Context
) : AbstractApk(commonFiles, streamCopier, ioDispatcher, applicationContext) {

	private val scriptsPatchOperation = scriptsPatchOperationFactory.create(
		this,
		patchRepository,
		patchFiles.scripts
	)

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
			emitStatus(LocalizedString.resource(R.string.status_comparing_apk))
			if (commonFiles.signedApk.verify().invoke()) {
				emitProgressDelta(scriptsPatchOperation.progressMax)
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