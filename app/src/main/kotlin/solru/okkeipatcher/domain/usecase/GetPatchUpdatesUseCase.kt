package solru.okkeipatcher.domain.usecase

import solru.okkeipatcher.data.patchupdates.PatchUpdates

interface GetPatchUpdatesUseCase {
	suspend operator fun invoke(): PatchUpdates
}