package ru.solrudev.okkeipatcher.data.worker

import android.app.PendingIntent
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.operation.factory.DownloadUpdateOperationFactory
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message

private val workLabel = LocalizedString.resource(R.string.notification_title_downloading_update)

private val successMessage = Message(
	LocalizedString.resource(R.string.notification_title_downloading_update_success),
	LocalizedString.resource(R.string.notification_message_downloading_update_success)
)

private val failureMessage = Message(
	LocalizedString.resource(R.string.notification_title_downloading_update_failed),
	LocalizedString.resource(R.string.notification_message_downloading_update_failed)
)

@HiltWorker
class DownloadUpdateWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationServiceFactory: NotificationServiceFactory,
	workManager: WorkManager,
	downloadUpdateOperationFactory: DownloadUpdateOperationFactory
) : ForegroundOperationWorker(
	context, workerParameters, notificationServiceFactory, workManager, downloadUpdateOperationFactory,
	WorkNotificationsParameters(workLabel, successMessage, failureMessage)
) {

	override fun createNotificationsContentIntent(): PendingIntent {
		return NavDeepLinkBuilder(applicationContext)
			.setGraph(R.navigation.okkei_nav_graph)
			.setDestination(R.id.update_fragment)
			.createPendingIntent()
	}
}