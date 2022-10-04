package ru.solrudev.okkeipatcher.domain.model

import ru.solrudev.okkeipatcher.domain.model.patchupdates.PatchUpdates

data class PatchParameters(
	val handleSaveData: Boolean,
	val patchUpdates: PatchUpdates,
	val patchVersion: String
)