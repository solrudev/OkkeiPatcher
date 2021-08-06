package solru.okkeipatcher.core.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.core.PatchService

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val patchService: PatchService
) : BaseWorker(context, workerParameters, R.string.notification_title_patch, patchService) {

	override suspend fun doServiceWork() {
		patchService.patch(manifest, config)
	}

	companion object {
		const val WORK_NAME = "OKKEIPATCHER_PATCH_WORK"
	}
}