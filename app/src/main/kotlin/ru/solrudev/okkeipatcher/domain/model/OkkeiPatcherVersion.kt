package ru.solrudev.okkeipatcher.domain.model

data class OkkeiPatcherVersion(
	val versionName: String,
	val changes: List<String>
)