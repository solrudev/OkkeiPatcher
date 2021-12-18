package solru.okkeipatcher.core.files.impl.english

import solru.okkeipatcher.R
import solru.okkeipatcher.core.files.base.Obb
import solru.okkeipatcher.core.impl.english.FileVersionKey
import solru.okkeipatcher.core.impl.english.PatchFile
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.model.Language
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.files.common.CommonFileHashKey
import solru.okkeipatcher.model.files.common.CommonFiles
import solru.okkeipatcher.model.manifest.OkkeiManifest
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class ObbEnglish @Inject constructor(commonFiles: CommonFiles) : Obb(commonFiles) {

	override suspend fun patch(manifest: OkkeiManifest) {
		progressProvider.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (commonFiles.obbToPatch.verify()) return
		downloadObb(manifest)
	}

	override suspend fun update(manifest: OkkeiManifest) {
		progressProvider.mutableProgress.reset()
		commonFiles.obbToPatch.delete()
		downloadObb(manifest)
	}

	private suspend inline fun downloadObb(manifest: OkkeiManifest) {
		try {
			mutableStatus.emit(LocalizedString.resource(R.string.status_downloading_obb))
			try {
				commonFiles.obbToPatch.downloadFrom(
					manifest.patches[Language.English]?.get(
						PatchFile.Obb.name
					)?.url!!
				)
			} catch (e: Throwable) {
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), e)
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			val obbHash = commonFiles.obbToPatch.computeHash()
			if (obbHash != manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_hash_obb_mismatch))
			}
			Preferences.set(CommonFileHashKey.patched_obb_hash.name, obbHash)
			Preferences.set(
				FileVersionKey.obb_version.name,
				manifest.patches[Language.English]?.get(PatchFile.Obb.name)?.version!!
			)
		} catch (e: Throwable) {
			commonFiles.obbToPatch.delete()
			throw e
		}
	}
}