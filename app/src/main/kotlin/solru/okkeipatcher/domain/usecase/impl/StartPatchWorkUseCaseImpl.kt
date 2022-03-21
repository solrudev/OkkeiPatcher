package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.domain.usecase.StartPatchWorkUseCase
import solru.okkeipatcher.workers.PatchWorker
import javax.inject.Inject

class StartPatchWorkUseCaseImpl @Inject constructor() : StartPatchWorkUseCase {

	override fun invoke(): Work {
		val workRequest = OneTimeWorkRequest.from(PatchWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			PatchWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		return Work(workRequest.id)
	}
}