package ru.solrudev.okkeipatcher.domain.service.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile

interface ScriptsPatchOperationFactory {
	fun create(scriptsPatchFile: PatchFile): Operation<Unit>
}