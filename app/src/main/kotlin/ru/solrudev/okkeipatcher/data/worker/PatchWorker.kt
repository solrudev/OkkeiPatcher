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
import ru.solrudev.okkeipatcher.domain.operation.factory.PatchOperationFactory

private val workLabel = LocalizedString.resource(R.string.notification_title_patch)

private val successMessage = Message(
	LocalizedString.resource(R.string.notification_title_patch_completed),
	LocalizedString.resource(R.string.notification_message_patch_success)
)

private val failureMessage = Message(
	LocalizedString.resource(R.string.notification_title_patch_completed),
	LocalizedString.resource(R.string.notification_message_patch_failed)
)

@HiltWorker
class PatchWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationServiceFactory: NotificationServiceFactory,
	workManager: WorkManager,
	patchOperationFactory: PatchOperationFactory
) : ForegroundOperationWorker(
	context, workerParameters, notificationServiceFactory, workManager, patchOperationFactory,
	WorkNotificationsParameters(workLabel, successMessage, failureMessage)
) {
	override fun createNotificationsContentIntent() = workNotificationIntent()
}