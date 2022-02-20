package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.StartPatchWorkUseCase
import solru.okkeipatcher.domain.workers.PatchWorker
import java.util.*
import javax.inject.Inject

class StartPatchWorkUseCaseImpl @Inject constructor() : StartPatchWorkUseCase {

	override fun invoke(): UUID {
		val workRequest = OneTimeWorkRequest.from(PatchWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			PatchWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		return workRequest.id
	}
}