package solru.okkeipatcher.domain.worker

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.model.LocalizedString
import solru.okkeipatcher.domain.service.RestoreService
import solru.okkeipatcher.util.Preferences

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val restoreService: RestoreService
) : ForegroundWorker(context, workerParameters, restoreService) {

	override val progressNotificationTitle = LocalizedString.resource(R.string.notification_title_restore)

	override suspend fun doServiceWork() {
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		restoreService.restore(processSaveData)
	}

	override fun createPendingIntent() = NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.okkei_nav_graph)
		.setDestination(R.id.restore_fragment)
		.createPendingIntent()

	companion object {
		const val WORK_NAME = "OKKEI_PATCHER_RESTORE_WORK"
	}
}