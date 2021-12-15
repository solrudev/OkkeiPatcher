package solru.okkeipatcher.core.files.impl.english

import solru.okkeipatcher.R
import solru.okkeipatcher.core.files.base.Obb
import solru.okkeipatcher.core.impl.english.FileVersionKey
import solru.okkeipatcher.core.impl.english.PatchFile
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFileInstances
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class ObbEnglish @Inject constructor(commonFileInstances: CommonFileInstances) :
	Obb(commonFileInstances) {

	override suspend fun patch(manifest: OkkeiManifest) {
		progressProvider.mutableProgress.reset()
		statusMutable.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (commonFileInstances.obbToPatch.verify()) return
		downloadObb(manifest)
	}

	override suspend fun update(manifest: OkkeiManifest) {
		progressProvider.mutableProgress.reset()
		commonFileInstances.obbToPatch.delete()
		downloadObb(manifest)
	}

	private suspend inline fun downloadObb(manifest: OkkeiManifest) {
		try {
			statusMutable.emit(LocalizedString.resource(R.string.status_downloading_obb))
			commonFileInstances.obbToPatch.downloadAndWrapException(
				manifest.patches[Language.English]?.get(
					PatchFile.Obb.name
				)?.url!!
			)
			statusMutable.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			val obbHash = commonFileInstances.obbToPatch.computeHash()
			if (obbHash != manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.hash) {
				throwErrorMessage(R.string.error_hash_obb_mismatch)
			}
			Preferences.set(CommonFileHashKey.patched_obb_hash.name, obbHash)
			Preferences.set(
				FileVersionKey.obb_version.name,
				manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.version!!
			)
		} catch (e: Throwable) {
			commonFileInstances.obbToPatch.delete()
			throw e
		}
	}
}