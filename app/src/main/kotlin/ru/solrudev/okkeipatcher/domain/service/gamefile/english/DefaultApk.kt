package ru.solrudev.okkeipatcher.domain.service.gamefile.english

import io.github.solrudev.simpleinstaller.PackageInstaller
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.core.operation.aggregateOperation
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ApkRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.Apk
import ru.solrudev.okkeipatcher.domain.service.gamefile.ApkZipPackage
import ru.solrudev.okkeipatcher.domain.service.operation.factory.ScriptsPatchOperationFactory
import javax.inject.Inject

class DefaultApk @Inject constructor(
	patchRepository: DefaultPatchRepository,
	scriptsPatchOperationFactory: ScriptsPatchOperationFactory,
	apkRepository: ApkRepository,
	apkZipPackage: ApkZipPackage,
	packageInstaller: PackageInstaller
) : Apk(apkRepository, apkZipPackage, packageInstaller) {

	private val scriptsPatchOperation = scriptsPatchOperationFactory.create(apkZipPackage, patchRepository.scripts)

	override fun patch(): Operation<Unit> {
		val installPatchedOperation = installPatched(updating = false)
		return operation(scriptsPatchOperation, installPatchedOperation) {
			status(LocalizedString.resource(R.string.status_comparing_apk))
			if (apkRepository.tempApk.verify()) {
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
			apkRepository.tempApk.delete()
		},
		scriptsPatchOperation,
		installPatched(updating = true)
	)
}