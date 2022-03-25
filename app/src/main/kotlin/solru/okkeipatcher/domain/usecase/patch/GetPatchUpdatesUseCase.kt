package solru.okkeipatcher.domain.usecase.patch

import solru.okkeipatcher.domain.model.patchupdates.PatchUpdates

interface GetPatchUpdatesUseCase {
	suspend operator fun invoke(): PatchUpdates
}