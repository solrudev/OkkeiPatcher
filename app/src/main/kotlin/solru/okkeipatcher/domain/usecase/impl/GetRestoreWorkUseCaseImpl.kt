package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.data.asWork
import solru.okkeipatcher.domain.usecase.GetRestoreWorkUseCase
import solru.okkeipatcher.workers.RestoreWorker
import javax.inject.Inject

class GetRestoreWorkUseCaseImpl @Inject constructor() : GetRestoreWorkUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(RestoreWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}