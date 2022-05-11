package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import javax.inject.Inject

interface GetPatchSizeInMbUseCase {
	suspend operator fun invoke(): Double
}

class GetPatchSizeInMbUseCaseImpl @Inject constructor(
	private val patchRepositoryFactory: PatchRepositoryFactory
) : GetPatchSizeInMbUseCase {

	override suspend fun invoke(): Double {
		val patchRepository = patchRepositoryFactory.create()
		return patchRepository.getPatchSizeInMb()
	}
}