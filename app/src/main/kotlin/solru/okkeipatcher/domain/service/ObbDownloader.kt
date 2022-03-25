package solru.okkeipatcher.domain.service

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.base.ObservableImpl
import solru.okkeipatcher.domain.exception.LocalizedException
import solru.okkeipatcher.domain.file.common.CommonFileHashKey
import solru.okkeipatcher.domain.file.common.CommonFiles
import solru.okkeipatcher.domain.gamefile.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.repository.patch.ObbDataRepository
import solru.okkeipatcher.io.service.HttpDownloader
import solru.okkeipatcher.util.Preferences

class ObbDownloader @AssistedInject constructor(
	@Assisted private val obbDataRepository: ObbDataRepository,
	@Assisted private val commonFiles: CommonFiles,
	private val httpDownloader: HttpDownloader
) : ObservableImpl() {

	override val progress = merge(super.progress, commonFiles.obbToPatch.progress)

	suspend fun download() {
		val obb = commonFiles.obbToPatch
		try {
			_status.emit(LocalizedString.resource(R.string.status_downloading_obb))
			val obbData = obbDataRepository.getObbData()
			val obbHash: String
			try {
				obb.delete()
				obb.create()
				val outputStream = obb.createOutputStream()
				obbHash = httpDownloader.download(obbData.url, outputStream, hashing = true) { progressData ->
					progressPublisher._progress.emit(progressData)
				}
			} catch (t: Throwable) {
				obb.delete()
				if (t is CancellationException) {
					throw t
				}
				throw LocalizedException(LocalizedString.resource(R.string.error_http_file_download), cause = t)
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