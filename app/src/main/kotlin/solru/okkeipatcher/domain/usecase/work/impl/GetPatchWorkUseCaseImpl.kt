package solru.okkeipatcher.domain.usecase.work.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.model.asWork
import solru.okkeipatcher.domain.usecase.work.GetPatchWorkUseCase
import solru.okkeipatcher.domain.worker.PatchWorker
import javax.inject.Inject

class GetPatchWorkUseCaseImpl @Inject constructor() : GetPatchWorkUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(PatchWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}