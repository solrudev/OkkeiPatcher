package solru.okkeipatcher.data.manifest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileData(
	@Json(name = "Version") val version: Int,
	@Json(name = "URL") val url: String,
	@Json(name = "SHA256") val hash: String,
	@Json(name = "Size") val size: Long
)