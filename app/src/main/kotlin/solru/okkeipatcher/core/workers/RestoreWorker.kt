package solru.okkeipatcher.core.workers

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.services.RestoreService
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.data.ServiceConfig
import solru.okkeipatcher.utils.Preferences

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val restoreService: RestoreService
) : BaseWorker(
	context,
	workerParameters,
	LocalizedString.resource(R.string.notification_title_restore),
	restoreService
) {

	override suspend fun doServiceWork() {
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val config = ServiceConfig(processSaveData)
		restoreService.restore(config)
	}

	companion object {
		const val WORK_NAME = "OkkeiPatcher_RESTORE_WORK"
	}
}