package solru.okkeipatcher.domain.usecase.patch.impl.english

import solru.okkeipatcher.domain.repository.patch.DefaultPatchRepository
import solru.okkeipatcher.domain.usecase.patch.GetPatchSizeInMbUseCase
import solru.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import solru.okkeipatcher.util.extension.round
import javax.inject.Inject

class DefaultGetPatchSizeInMbUseCase @Inject constructor(
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val patchRepository: DefaultPatchRepository
) : GetPatchSizeInMbUseCase {

	override suspend fun invoke(): Double {
		try {
			val scriptsSize = patchRepository.getScriptsData().size / 1_048_576.0
			val obbSize = patchRepository.getObbData().size / 1_048_576.0
			val patchUpdates = getPatchUpdatesUseCase()
			if (!patchUpdates.available) {
				return (scriptsSize + obbSize).round(2)
			}
			val scriptsUpdateSize = if (patchUpdates.apkUpdatesAvailable) scriptsSize else 0.0
			val obbUpdateSize = if (patchUpdates.obbUpdatesAvailable) obbSize else 0.0
			return (scriptsUpdateSize + obbUpdateSize).round(2)
		} catch (t: Throwable) {
			return -1.0
		}
	}
}