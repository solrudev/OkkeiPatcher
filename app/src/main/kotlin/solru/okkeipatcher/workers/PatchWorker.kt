package solru.okkeipatcher.workers

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.data.LocalizedString
import solru.okkeipatcher.domain.AppKey
import solru.okkeipatcher.domain.services.PatchService
import solru.okkeipatcher.domain.usecase.GetPatchUpdatesUseCase
import solru.okkeipatcher.utils.Preferences

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val patchService: PatchService,
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase
) : ForegroundWorker(context, workerParameters, patchService) {

	override val progressNotificationTitle = LocalizedString.resource(R.string.notification_title_patch)

	override suspend fun doServiceWork() {
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val patchUpdates = getPatchUpdatesUseCase()
		patchService.patch(processSaveData, patchUpdates)
	}

	override fun createDeepLinkPendingIntent() = NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.okkei_nav_graph)
		.setDestination(R.id.patch_fragment)
		.createPendingIntent()

	companion object {
		const val WORK_NAME = "OKKEI_PATCHER_PATCH_WORK"
	}
}