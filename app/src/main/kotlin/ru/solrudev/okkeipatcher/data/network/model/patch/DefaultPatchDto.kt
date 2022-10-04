package ru.solrudev.okkeipatcher.data.network.model.patch

import com.squareup.moshi.JsonClass
import ru.solrudev.okkeipatcher.data.network.model.FileDto

@JsonClass(generateAdapter = true)
data class DefaultPatchDto(
	val displayVersion: String,
	val scripts: FileDto,
	val obb: FileDto
)