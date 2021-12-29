package solru.okkeipatcher.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileData(
	val version: Int,
	val url: String,
	val hash: String,
	val size: Long
)