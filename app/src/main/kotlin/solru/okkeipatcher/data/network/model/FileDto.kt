package solru.okkeipatcher.data.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileDto(
	val version: Int,
	val url: String,
	val hash: String,
	val size: Long
)