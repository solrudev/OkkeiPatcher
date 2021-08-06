package solru.okkeipatcher.core.workers

import android.content.Context
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.core.RestoreService

class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val restoreService: RestoreService
) : BaseWorker(context, workerParameters, R.string.notification_title_restore, restoreService) {

	override suspend fun doServiceWork() {
		restoreService.restore()
	}

	companion object {
		const val WORK_NAME = "OKKEIPATCHER_RESTORE_WORK"
	}
}