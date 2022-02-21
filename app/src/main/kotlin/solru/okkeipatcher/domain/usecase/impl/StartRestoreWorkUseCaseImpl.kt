package solru.okkeipatcher.domain.usecase.impl

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.StartRestoreWorkUseCase
import solru.okkeipatcher.workers.RestoreWorker
import java.util.*
import javax.inject.Inject

class StartRestoreWorkUseCaseImpl @Inject constructor() : StartRestoreWorkUseCase {

	override fun invoke(): UUID {
		val workRequest = OneTimeWorkRequest.from(RestoreWorker::class.java)
		WorkManager.getInstance(OkkeiApplication.context).enqueueUniqueWork(
			RestoreWorker.WORK_NAME,
			ExistingWorkPolicy.KEEP,
			workRequest
		)
		return workRequest.id
	}
}