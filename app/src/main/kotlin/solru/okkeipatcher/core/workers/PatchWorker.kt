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
import solru.okkeipatcher.core.strategy.PatchInfoStrategy
import solru.okkeipatcher.model.LocalizedString
import solru.okkeipatcher.model.dto.ServiceConfig
import solru.okkeipatcher.repository.ManifestRepository
import solru.okkeipatcher.utils.Preferences

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val patchService: PatchService,
	private val manifestRepository: ManifestRepository,
	private val patchInfoStrategy: PatchInfoStrategy
) : BaseWorker(context, workerParameters, LocalizedString.resource(R.string.notification_title_patch), patchService) {

	override suspend fun doServiceWork() {
		val manifest = manifestRepository.getManifest()
		val processSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val patchUpdates = patchInfoStrategy.patchUpdates(manifest)
		val config = ServiceConfig(processSaveData, patchUpdates)
		patchService.patch(manifest, config)
	}

	companion object {
		const val WORK_NAME = "OkkeiPatcher_PATCH_WORK"
	}
}