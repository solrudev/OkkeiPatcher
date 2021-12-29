package solru.okkeipatcher.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OkkeiPatcherChangelog(

	/**
	 * Okkei Patcher changelog. Keys are version strings and values are lists of changes in the version.
	 */
	val changelog: Map<String, List<String>>
)