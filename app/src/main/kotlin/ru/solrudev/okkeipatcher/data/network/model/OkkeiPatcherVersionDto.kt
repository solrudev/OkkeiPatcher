package ru.solrudev.okkeipatcher.data.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OkkeiPatcherVersionDto(
	val versionName: String,
	val changes: List<String>
)