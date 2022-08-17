package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.service.NotificationService
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.service.operation.factory.RestoreOperationFactory

private val workLabel = LocalizedString.resource(R.string.notification_title_restore)

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationService: NotificationService,
	workManager: WorkManager,
	restoreOperationFactory: RestoreOperationFactory
) : ForegroundWorker(context, workerParameters, notificationService, workManager, restoreOperationFactory, workLabel)