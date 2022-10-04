package ru.solrudev.okkeipatcher.domain.model

data class OkkeiPatcherUpdateData(
	val isAvailable: Boolean,
	val sizeInMb: Double,
	val changelog: List<OkkeiPatcherVersion>
)