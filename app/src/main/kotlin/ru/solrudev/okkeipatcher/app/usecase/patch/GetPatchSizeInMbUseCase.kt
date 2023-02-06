package ru.solrudev.okkeipatcher.app.usecase.patch

import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory
import javax.inject.Inject

class GetPatchSizeInMbUseCase @Inject constructor(private val patchRepositoryFactory: PatchRepositoryFactory) {
	suspend operator fun invoke(): Double {
		val patchRepository = patchRepositoryFactory.create()
		return patchRepository.getPatchSizeInMb()
	}
}