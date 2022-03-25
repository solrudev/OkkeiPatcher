package solru.okkeipatcher.domain.usecase.work.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.model.asWork
import solru.okkeipatcher.domain.usecase.work.GetRestoreWorkUseCase
import solru.okkeipatcher.domain.worker.RestoreWorker
import javax.inject.Inject

class GetRestoreWorkUseCaseImpl @Inject constructor() : GetRestoreWorkUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(RestoreWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.asWork()
}