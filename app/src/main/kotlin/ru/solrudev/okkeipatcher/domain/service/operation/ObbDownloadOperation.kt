package ru.solrudev.okkeipatcher.domain.service.operation

import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.core.operation.operation
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.model.exception.LocalizedException
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchFile
import ru.solrudev.okkeipatcher.domain.service.HttpDownloader
import ru.solrudev.okkeipatcher.domain.service.download
import java.io.File

private const val PROGRESS_MULTIPLIER = 10

@Suppress("FunctionName")
fun ObbDownloadOperation(
	obb: File,
	obbPatchFile: PatchFile,
	httpDownloader: HttpDownloader
) = operation(progressMax = httpDownloader.progressMax * PROGRESS_MULTIPLIER) {
	try {
		status(LocalizedString.resource(R.string.status_downloading_obb))
		val obbData = obbPatchFile.getData()
		val obbHash = httpDownloader.download(obbData.url, obb, hashing = true) { progressDelta ->
			progressDelta(progressDelta * PROGRESS_MULTIPLIER)
		}
		if (obbHash != obbData.hash) {
			throw LocalizedException(LocalizedString.resource(R.string.error_hash_obb_mismatch))
		}
		obbPatchFile.installedVersion.persist(obbData.version)
	} catch (t: Throwable) {
		obb.delete()
		throw t
	}
}