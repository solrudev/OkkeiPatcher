package solru.okkeipatcher.core.services.gamefile.impl.english

import solru.okkeipatcher.R
import solru.okkeipatcher.core.model.files.common.CommonFileHashKey
import solru.okkeipatcher.core.model.files.common.CommonFiles
import solru.okkeipatcher.core.services.gamefile.impl.BaseObb
import solru.okkeipatcher.core.strategy.impl.english.FileVersionKey
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.repository.patch.EnglishPatchRepository
import solru.okkeipatcher.utils.Preferences
import solru.okkeipatcher.utils.extensions.reset
import javax.inject.Inject

class EnglishObb @Inject constructor(
	private val patchRepository: EnglishPatchRepository,
	private val httpDownloader: HttpDownloader,
	commonFiles: CommonFiles
) : BaseObb(commonFiles) {

	override suspend fun patch() {
		progressPublisher.mutableProgress.reset()
		mutableStatus.emit(LocalizedString.resource(R.string.status_comparing_obb))
		if (commonFiles.obbToPatch.verify()) return
		downloadObb()
	}

	override suspend fun update() {
		progressPublisher.mutableProgress.reset()
		commonFiles.obbToPatch.delete()
		downloadObb()
	}

	private suspend inline fun downloadObb() {
		val obb = commonFiles.obbToPatch
		try {
			mutableStatus.emit(LocalizedString.resource(R.string.status_downloading_obb))
			val obbData = patchRepository.getObbData()
			val obbHash: String
			try {
				obb.delete()
				obb.create()
				val outputStream = obb.createOutputStream()
				obbHash = httpDownloader.download(obbData.url, outputStream, hashing = true) { progressData ->
					progressPublisher.mutableProgress.emit(progressData)
				}
			} catch (e: Throwable) {
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
			}
			mutableStatus.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			if (obbHash != obbData.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_hash_obb_mismatch))
			}
			Preferences.set(CommonFileHashKey.patched_obb_hash.name, obbHash)
			Preferences.set(FileVersionKey.obb_version.name, obbData.version)
		} catch (e: Throwable) {
			obb.delete()
			throw e
		}
	}
}