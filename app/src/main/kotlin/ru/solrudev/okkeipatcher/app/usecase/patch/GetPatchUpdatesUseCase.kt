package ru.solrudev.okkeipatcher.app.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import javax.inject.Inject

class GetPatchUpdatesUseCase @Inject constructor(private val patchRepositoryFactory: PatchRepositoryFactory) {
	suspend operator fun invoke(refresh: Boolean): PatchUpdates {
		val patchRepository = patchRepositoryFactory.create()
		return patchRepository.getPatchUpdates(refresh)
	}
}