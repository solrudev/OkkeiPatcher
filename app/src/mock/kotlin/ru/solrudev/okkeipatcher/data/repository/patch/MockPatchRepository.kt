package ru.solrudev.okkeipatcher.data.repository.patch

import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.domain.model.patchupdates.DefaultPatchUpdates
import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class MockPatchRepository @Inject constructor() : PatchRepository {

	private var firstUpdatesCheck = true

	override suspend fun getDisplayVersion() = "1.0(mock)"

	override suspend fun getPatchUpdates(): PatchUpdates {
		delay(3.seconds)
		val isUpdateAvailable = firstUpdatesCheck
		firstUpdatesCheck = false
		return DefaultPatchUpdates(isUpdateAvailable)
	}

	override suspend fun getPatchSizeInMb(): Double {
		delay(1.seconds)
		return 100.0
	}

	override suspend fun clearPersistedData() {}
}