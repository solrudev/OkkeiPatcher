package solru.okkeipatcher.domain.usecase.impl

import androidx.work.WorkManager
import solru.okkeipatcher.OkkeiApplication
import solru.okkeipatcher.domain.usecase.GetRestoreWorkIdUseCase
import solru.okkeipatcher.workers.RestoreWorker
import javax.inject.Inject

class GetRestoreWorkIdUseCaseImpl @Inject constructor() : GetRestoreWorkIdUseCase {

	override fun invoke() = WorkManager.getInstance(OkkeiApplication.context)
		.getWorkInfosForUniqueWork(RestoreWorker.WORK_NAME)
		.get()
		.firstOrNull()
		?.id
}