package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.service.operation.factory.RestoreOperationFactory

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	restoreOperationFactory: RestoreOperationFactory
) : ForegroundWorker(context, workerParameters, restoreOperationFactory) {

	override val workTitle = LocalizedString.resource(R.string.notification_title_restore)
	override val destinationScreen = R.id.restore_fragment
}