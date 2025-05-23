/*
 * Okkei Patcher
 * Copyright (C) 2023-2024 Ilya Fomichev
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

private val initialStatus = LocalizedString.resource(R.string.status_checking)

@HiltWorker
class RestoreWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationServiceFactory: NotificationServiceFactory,
	workManager: WorkManager,
	restoreOperationFactory: RestoreOperationFactory
) : ForegroundOperationWorker(
	context, workerParameters, workManager, restoreOperationFactory,
	notificationServiceFactory.create(
		workLabel,
		contentIntent = workNotificationIntent(context, workerParameters),
		cancelIntent = workNotificationIntent(context, workerParameters, isAbortRequested = true),
		showGameIconInProgressNotification = true
	),
	WorkNotificationsParameters(successMessage, failureMessage, initialStatus)
)