package solru.okkeipatcher.data.network.model.patch

import com.squareup.moshi.JsonClass
import solru.okkeipatcher.data.network.model.FileDto

@JsonClass(generateAdapter = true)
data class DefaultPatchDto(
	val scripts: FileDto,
	val obb: FileDto
)