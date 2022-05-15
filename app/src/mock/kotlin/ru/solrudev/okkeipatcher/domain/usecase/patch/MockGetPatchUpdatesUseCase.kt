package ru.solrudev.okkeipatcher.domain.usecase.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import javax.inject.Inject

class MockGetPatchUpdatesUseCase @Inject constructor() : GetPatchUpdatesUseCase {
	override suspend fun invoke() = DefaultPatchUpdates()
}