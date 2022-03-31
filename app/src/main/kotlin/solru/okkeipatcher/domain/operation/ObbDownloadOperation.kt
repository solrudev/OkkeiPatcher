package solru.okkeipatcher.domain.operation

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.repository.patch.ObbDataRepository
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.util.Preferences

class ObbDownloadOperation @AssistedInject constructor(
	@Assisted private val obbDataRepository: ObbDataRepository,
	@Assisted private val commonFiles: CommonFiles,
	private val httpDownloader: HttpDownloader
) : AbstractOperation<Unit>() {

	override val progressDelta = _progressDelta.map { it * 10 }
	override val progressMax = 100 * 10

	override suspend fun invoke() {
		val obb = commonFiles.obbToPatch
		try {
			_status.emit(LocalizedString.resource(R.string.status_downloading_obb))
			val obbData = obbDataRepository.getObbData()
			obb.delete()
			obb.create()
			val outputStream = obb.createOutputStream()
			val obbHash = httpDownloader.download(obbData.url, outputStream, hashing = true) { progressDelta ->
				_progressDelta.emit(progressDelta)
			}
			_status.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			if (obbHash != obbData.hash) {
				throw LocalizedException(LocalizedString.resource(R.string.error_hash_obb_mismatch))
			}
			Preferences.set(CommonFileHashKey.patched_obb_hash.name, obbHash)
			Preferences.set(PatchFileVersionKey.obb_version.name, obbData.version)
		} catch (t: Throwable) {
			obb.delete()
			throw t
		}
	}
}