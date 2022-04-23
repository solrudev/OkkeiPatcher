package ru.solrudev.okkeipatcher.domain.usecase.patch

import javax.inject.Inject

class MockGetPatchSizeInMbUseCase @Inject constructor() : GetPatchSizeInMbUseCase {

	override suspend fun invoke() = 100.0
}