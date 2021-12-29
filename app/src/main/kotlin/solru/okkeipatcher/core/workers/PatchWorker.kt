package solru.okkeipatcher.core.workers

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.services.PatchService
import solru.okkeipatcher.core.strategy.PatchDataStrategy
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.utils.Preferences

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val patchService: PatchService,
	private val patchDataStrategy: PatchDataStrategy
) : BaseWorker(context, workerParameters, LocalizedString.resource(R.string.notification_title_patch), patchService) {

	override suspend fun doServiceWork() {
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val patchUpdates = patchDataStrategy.getPatchUpdates()
		patchService.patch(processSaveData, patchUpdates)
	}

	companion object {
		const val WORK_NAME = "OKKEI_PATCHER_PATCH_WORK"
	}
}