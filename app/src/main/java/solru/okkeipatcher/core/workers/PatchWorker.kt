package solru.okkeipatcher.core.workers

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import solru.okkeipatcher.R
import solru.okkeipatcher.core.AppKey
import solru.okkeipatcher.core.ManifestRepository
import solru.okkeipatcher.core.PatchService
import solru.okkeipatcher.core.base.PatchInfoStrategy
import solru.okkeipatcher.model.dto.AppServiceConfig
import solru.okkeipatcher.utils.Preferences

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val patchService: PatchService,
	private val manifestRepository: ManifestRepository,
	private val patchInfoStrategy: PatchInfoStrategy
) : BaseWorker(context, workerParameters, R.string.notification_title_patch, patchService) {

	override suspend fun doServiceWork() {
		val manifest = manifestRepository.getManifest()
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val patchUpdates = patchInfoStrategy.patchUpdates(manifest)
		val config = AppServiceConfig(processSaveData, patchUpdates)
		patchService.patch(manifest, config)
	}

	companion object {
		const val WORK_NAME = "OKKEIPATCHER_PATCH_WORK"
	}
}