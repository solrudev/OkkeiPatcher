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

	private var checkCount = 0

	override suspend fun getDisplayVersion() = "1.0(mock)"

	override suspend fun getPatchUpdates(refresh: Boolean): PatchUpdates {
		delay(3.seconds)
		val isUpdateAvailable = checkCount++ % 2 == 0
		return DefaultPatchUpdates(isUpdateAvailable)
	}

	override suspend fun getPatchSizeInMb(): Double {
		delay(1.seconds)
		return 100.0
	}

	override suspend fun clearPersistedData() {}
}