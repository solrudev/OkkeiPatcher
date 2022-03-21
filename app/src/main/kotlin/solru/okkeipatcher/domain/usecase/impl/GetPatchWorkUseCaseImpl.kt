package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.asWork
import solru.okkeipatcher.domain.usecase.GetPatchWorkUseCase
import solru.okkeipatcher.workers.PatchWorker
import javax.inject.Inject

class GetPatchWorkUseCaseImpl @Inject constructor() : GetPatchWorkUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(PatchWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}