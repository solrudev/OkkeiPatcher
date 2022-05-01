package ru.solrudev.okkeipatcher.domain.service.operation

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.file.common.CommonFileHashKey
import ru.solrudev.okkeipatcher.domain.file.common.CommonFiles
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.repository.patch.ObbDataRepository
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.impl.english.PatchFileVersionKey
import ru.solrudev.okkeipatcher.io.service.HttpDownloader
import ru.solrudev.okkeipatcher.util.Preferences

class ObbDownloadOperation @AssistedInject constructor(
	@Assisted private val obbDataRepository: ObbDataRepository,
	@Assisted private val commonFiles: CommonFiles,
	private val httpDownloader: HttpDownloader
) : AbstractOperation<Unit>() {

	private val progressMultiplier = 10
	override val progressMax = 100 * progressMultiplier

	override suspend fun invoke() {
		val obb = commonFiles.obbToPatch
		try {
			status(LocalizedString.resource(R.string.status_downloading_obb))
			val obbData = obbDataRepository.getObbData()
			obb.delete()
			obb.create()
			val outputStream = obb.createOutputStream()
			val obbHash = httpDownloader.download(obbData.url, outputStream, hashing = true) { progressDelta ->
				progressDelta(progressDelta * progressMultiplier)
			}
			status(LocalizedString.resource(R.string.status_writing_obb_hash))
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