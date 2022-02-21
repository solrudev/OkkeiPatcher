package solru.okkeipatcher.domain.services

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.merge
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.domain.model.files.common.CommonFileHashKey
import solru.okkeipatcher.domain.model.files.common.CommonFiles
import solru.okkeipatcher.domain.strategy.impl.english.PatchFileVersionKey
import solru.okkeipatcher.exceptions.OkkeiException
import solru.okkeipatcher.io.services.HttpDownloader
import solru.okkeipatcher.repository.patch.ObbDataRepository
import solru.okkeipatcher.utils.Preferences

class ObbDownloader @AssistedInject constructor(
	@Assisted private val obbDataRepository: ObbDataRepository,
	@Assisted private val commonFiles: CommonFiles,
	private val httpDownloader: HttpDownloader
) : ObservableServiceImpl() {

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
			} catch (e: Throwable) {
				obb.delete()
				throw OkkeiException(LocalizedString.resource(R.string.error_http_file_download), cause = e)
			}
			_status.emit(LocalizedString.resource(R.string.status_writing_obb_hash))
			if (obbHash != obbData.hash) {
				throw OkkeiException(LocalizedString.resource(R.string.error_hash_obb_mismatch))
			}
			Preferences.set(CommonFileHashKey.patched_obb_hash.name, obbHash)
			Preferences.set(PatchFileVersionKey.obb_version.name, obbData.version)
		} catch (e: Throwable) {
			obb.delete()
			throw e
		}
	}
}