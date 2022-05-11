package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import javax.inject.Inject

interface GetPatchUpdatesUseCase {
	suspend operator fun invoke(): PatchUpdates
}

class GetPatchUpdatesUseCaseImpl @Inject constructor(
	private val patchRepositoryFactory: PatchRepositoryFactory
) : GetPatchUpdatesUseCase {

	override suspend fun invoke(): PatchUpdates {
		val patchRepository = patchRepositoryFactory.create()
		return patchRepository.getPatchUpdates()
	}
}