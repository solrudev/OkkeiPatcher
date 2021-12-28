package solru.okkeipatcher.data.manifest

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import solru.okkeipatcher.core.model.Language
import solru.okkeipatcher.utils.extensions.isNotEmptyOrBlank

@JsonClass(generateAdapter = true)
data class OkkeiManifest(
	@Json(name = "Version") val version: Int,
	@Json(name = "OkkeiPatcher") val okkeiPatcher: OkkeiPatcherData,
	@Json(name = "Patches") val patches: Map<Language, Map<String, FileData>>
) {
	init {
		require(isValid()) { "Manifest is invalid" }
	}

	private fun isValid() = version > 0 &&
			okkeiPatcher.version > 0 &&
			okkeiPatcher.changelog.isNotEmptyOrBlank() &&
			okkeiPatcher.url.isNotEmptyOrBlank() &&
			okkeiPatcher.hash.isNotEmptyOrBlank() &&
			okkeiPatcher.size > 0 &&
			patches.count() > 0 && patches.values.all { files ->
		files.count() > 0 && files.values.all { file ->
			file.version > 0 &&
					file.url.isNotEmptyOrBlank() &&
					file.hash.isNotEmptyOrBlank() &&
					file.size > 0
		}
	}
}