package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates

interface GetPatchUpdatesUseCase {
	suspend operator fun invoke(): PatchUpdates
}