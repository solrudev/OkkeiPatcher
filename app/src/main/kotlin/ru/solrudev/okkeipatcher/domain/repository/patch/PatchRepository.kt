package ru.solrudev.okkeipatcher.domain.repository.patch

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates

interface PatchRepository {
	suspend fun getPatchUpdates(): PatchUpdates
	suspend fun getPatchSizeInMb(): Double
}