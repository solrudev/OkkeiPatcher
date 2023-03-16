package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.repository.app.work.workNotificationIntent
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.operation.factory.RestoreOperationFactory

private val workLabel = LocalizedString.resource(R.string.notification_title_restore)

private val successMessage = Message(
	LocalizedString.resource(R.string.notification_title_restore_completed),
	LocalizedString.resource(R.string.notification_message_restore_success)
)

private val failureMessage = Message(
	LocalizedString.resource(R.string.notification_title_restore_completed),
	LocalizedString.resource(R.string.notification_message_restore_failed)
)

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationServiceFactory: NotificationServiceFactory,
	workManager: WorkManager,
	restoreOperationFactory: RestoreOperationFactory
) : ForegroundOperationWorker(
	context, workerParameters, notificationServiceFactory, workManager, restoreOperationFactory,
	WorkNotificationsParameters(workLabel, successMessage, failureMessage)
) {
	override fun createNotificationsContentIntent() = workNotificationIntent()
}