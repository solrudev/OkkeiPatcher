package ru.solrudev.okkeipatcher.domain.operation.factory

import ru.solrudev.okkeipatcher.domain.core.operation.Operation
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile

interface ObbDownloadOperationFactory {
	fun create(obbPatchFile: PatchFile): Operation<Unit>
}