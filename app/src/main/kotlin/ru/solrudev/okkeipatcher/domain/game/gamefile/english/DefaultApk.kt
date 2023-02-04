package ru.solrudev.okkeipatcher.domain.game.gamefile.english

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.game.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.operation.factory.ScriptsPatchOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkBackupRepository
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatchOperationFactory: ScriptsPatchOperationFactory,
	apkRepository: ApkRepository,
	apkBackupRepository: ApkBackupRepository
) : Apk(apkRepository, apkBackupRepository) {

	private val scriptsPatchOperation = scriptsPatchOperationFactory.create(patchRepository.scripts)

	override fun patch(): Operation<Unit> {
		val installPatchedOperation = installPatched(updating = false)
		return operation(scriptsPatchOperation, installPatchedOperation) {
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (apkRepository.verifyTemp()) {
				progressDelta(scriptsPatchOperation.progressMax)
				installPatchedOperation()
				return@operation
			}
			scriptsPatchOperation()
			installPatchedOperation()
		}
	}

	override fun update() = aggregateOperation(
		operation {
			apkRepository.deleteTemp()
		},
		scriptsPatchOperation,
		installPatched(updating = true)
	)
}