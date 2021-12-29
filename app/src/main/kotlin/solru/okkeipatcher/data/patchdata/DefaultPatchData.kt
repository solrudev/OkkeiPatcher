package solru.okkeipatcher.data.patchdata

import com.squareup.moshi.JsonClass
import solru.okkeipatcher.data.FileData

@JsonClass(generateAdapter = true)
data class DefaultPatchData(
	val scripts: FileData,
	val obb: FileData
)