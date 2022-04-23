package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import javax.inject.Inject

class MockGetPatchUpdatesUseCase @Inject constructor(private val patchRepository: DefaultPatchRepository) :
	GetPatchUpdatesUseCase {

	override suspend fun invoke() = DefaultPatchUpdates()
}