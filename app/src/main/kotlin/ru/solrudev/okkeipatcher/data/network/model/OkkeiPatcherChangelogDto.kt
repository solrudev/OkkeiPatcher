package ru.solrudev.okkeipatcher.data.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OkkeiPatcherChangelogDto(

	/**
	 * Okkei Patcher changelog. Keys are version strings and values are lists of changes in the version.
	 */
	val changelog: Map<String, List<String>>
)