package ru.solrudev.okkeipatcher.domain.worker

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.AppKey
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.Operation
import ru.solrudev.okkeipatcher.domain.service.gamefile.strategy.GameFileStrategy
import ru.solrudev.okkeipatcher.domain.service.operation.PatchOperation
import ru.solrudev.okkeipatcher.domain.usecase.patch.GetPatchUpdatesUseCase
import ru.solrudev.okkeipatcher.util.Preferences

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val getPatchUpdatesUseCase: GetPatchUpdatesUseCase,
	private val strategy: GameFileStrategy
) : ForegroundWorker(context, workerParameters) {

	override val progressNotificationTitle = LocalizedString.resource(R.string.notification_title_patch)

	override suspend fun getOperation(): Operation<Unit> {
		val handleSaveData = Preferences.get(
			AppKey.process_save_data_enabled.name,
			Build.VERSION.SDK_INT < Build.VERSION_CODES.R
		)
		val patchUpdates = getPatchUpdatesUseCase()
		val patchOperation = PatchOperation(strategy, handleSaveData, patchUpdates)
		patchOperation.checkCanPatch()
		return patchOperation
	}

	override fun createPendingIntent() = NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.okkei_nav_graph)
		.setDestination(R.id.patch_fragment)
		.createPendingIntent()
}