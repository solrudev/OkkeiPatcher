package ru.solrudev.okkeipatcher.domain.usecase.patch

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

private var firstCheck = true

class MockGetPatchUpdatesUseCase @Inject constructor() : GetPatchUpdatesUseCase {
	override suspend fun invoke(): DefaultPatchUpdates {
		delay(3.seconds)
		val isUpdateAvailable = firstCheck
		firstCheck = false
		return DefaultPatchUpdates(isUpdateAvailable)
	}
}