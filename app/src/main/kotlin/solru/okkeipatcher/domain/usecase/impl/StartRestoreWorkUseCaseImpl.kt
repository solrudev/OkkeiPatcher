package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.Work
import solru.okkeipatcher.domain.usecase.StartRestoreWorkUseCase
import solru.okkeipatcher.workers.RestoreWorker
import javax.inject.Inject

class StartRestoreWorkUseCaseImpl @Inject constructor() : StartRestoreWorkUseCase {

	override fun invoke(): Work {
		val workRequest = OneTimeWorkRequest.from(RestoreWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			RestoreWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		return Work(workRequest.id)
	}
}