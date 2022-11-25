package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import javax.inject.Inject

class GetPatchUpdatesUseCase @Inject constructor(private val patchRepositoryFactory: PatchRepositoryFactory) {
	suspend operator fun invoke(): PatchUpdates {
		val patchRepository = patchRepositoryFactory.create()
		return patchRepository.getPatchUpdates()
	}
}