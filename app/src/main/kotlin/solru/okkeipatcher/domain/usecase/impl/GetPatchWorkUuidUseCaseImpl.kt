package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.GetPatchWorkUuidUseCase
import solru.okkeipatcher.workers.PatchWorker
import javax.inject.Inject

class GetPatchWorkUuidUseCaseImpl @Inject constructor() : GetPatchWorkUuidUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(PatchWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.id
}