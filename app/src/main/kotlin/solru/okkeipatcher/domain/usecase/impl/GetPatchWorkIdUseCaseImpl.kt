package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.GetPatchWorkIdUseCase
import solru.okkeipatcher.workers.PatchWorker
import javax.inject.Inject

class GetPatchWorkIdUseCaseImpl @Inject constructor() : GetPatchWorkIdUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(PatchWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.id
}