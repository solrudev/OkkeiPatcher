package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class MockGetPatchSizeInMbUseCase @Inject constructor() : GetPatchSizeInMbUseCase {

	override suspend fun invoke(): Double {
		delay(1.seconds)
		return 100.0
	}
}