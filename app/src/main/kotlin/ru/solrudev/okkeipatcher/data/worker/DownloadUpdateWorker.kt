/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
	context, workerParameters, workManager, downloadUpdateOperationFactory,
	notificationServiceFactory.create(
		workLabel,
		downloadUpdateNotificationContentIntent(context),
		showGameIconInProgressNotification = false
	),
	WorkNotificationsParameters(successMessage, failureMessage)
)

private fun downloadUpdateNotificationContentIntent(applicationContext: Context): PendingIntent {
	return NavDeepLinkBuilder(applicationContext)
		.setGraph(R.navigation.main_nav_graph)
		.setDestination(R.id.update_fragment)
		.createPendingIntent()
}