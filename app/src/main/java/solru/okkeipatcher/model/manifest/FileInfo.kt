package solru.okkeipatcher.model.manifest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileInfo(
	@SerialName("Version") val version: Int,
	@SerialName("URL") val url: String,
	@SerialName("SHA256") val hash: String,
	@SerialName("Size") val size: Long
)