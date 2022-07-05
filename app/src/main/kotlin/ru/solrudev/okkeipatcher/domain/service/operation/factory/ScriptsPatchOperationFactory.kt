package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.gamefile.ZipPackage

interface ScriptsPatchOperationFactory {
	fun create(apk: ZipPackage, scriptsPatchFile: PatchFile): Operation<Unit>
}