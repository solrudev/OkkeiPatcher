package solru.okkeipatcher.model.manifest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.utils.extensions.isNotEmptyOrBlank

@Serializable
data class OkkeiManifest(
	@SerialName("Version") val version: Int,
	@SerialName("OkkeiPatcher") val okkeiPatcher: OkkeiPatcherInfo,
	@SerialName("Patches") val patches: Map<Language, Map<String, FileInfo>>
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