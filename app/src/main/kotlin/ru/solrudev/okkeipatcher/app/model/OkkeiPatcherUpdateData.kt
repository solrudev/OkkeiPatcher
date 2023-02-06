package ru.solrudev.okkeipatcher.app.model

data class OkkeiPatcherUpdateData(
	val isAvailable: Boolean,
	val sizeInMb: Double,
	val changelog: List<OkkeiPatcherVersion>
)