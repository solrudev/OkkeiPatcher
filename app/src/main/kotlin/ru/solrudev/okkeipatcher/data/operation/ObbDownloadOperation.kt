package ru.solrudev.okkeipatcher.data.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.FileDownloader
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.exception.ObbCorruptedException
import ru.solrudev.okkeipatcher.domain.repository.gamefile.ObbRepository
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile

private const val PROGRESS_MULTIPLIER = 10

@Suppress("FunctionName")
fun ObbDownloadOperation(
	obbPatchFile: PatchFile,
	obbRepository: ObbRepository,
	fileDownloader: FileDownloader
) = operation(progressMax = fileDownloader.progressMax * PROGRESS_MULTIPLIER) {
	try {
		status(LocalizedString.resource(R.string.status_downloading_obb))
		val obbData = obbPatchFile.getData()
		val obbHash = fileDownloader.download(
			obbData.url, obbRepository.obbPath, hashing = true,
			onProgressDeltaChanged = { progressDelta(it * PROGRESS_MULTIPLIER) }
		)
		if (obbHash != obbData.hash) {
			throw ObbCorruptedException()
		}
		obbPatchFile.installedVersion.persist(obbData.version)
	} catch (t: Throwable) {
		obbRepository.deleteObb()
		throw t
	}
}