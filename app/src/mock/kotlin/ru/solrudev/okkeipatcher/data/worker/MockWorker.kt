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
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.repository.app.work.workNotificationIntent
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.core.factory.SuspendFactory
import ru.solrudev.okkeipatcher.domain.operation.factory.MockOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.PatchRepository

private val workLabel = LocalizedString.resource(R.string.notification_title_test)

private val successMessage = Message(
	LocalizedString.resource(R.string.notification_title_test_completed),
	LocalizedString.resource(R.string.notification_message_test_success)
)

private val failureMessage = Message(
	LocalizedString.resource(R.string.notification_title_test_completed),
	LocalizedString.resource(R.string.notification_message_test_failed)
)

@HiltWorker
class MockWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	notificationServiceFactory: NotificationServiceFactory,
	workManager: WorkManager,
	preferencesRepository: PreferencesRepository,
	patchRepositoryFactory: SuspendFactory<PatchRepository>
) : ForegroundOperationWorker(
	context, workerParameters, workManager,
	MockOperationFactory(
		patchRepositoryFactory,
		preferencesRepository.patchVersion,
		preferencesRepository.patchStatus,
		isPatchWork = "PatchWork" in workerParameters.tags
	),
	notificationServiceFactory.create(
		workLabel,
		contentIntent = workNotificationIntent(context, workerParameters),
		cancelIntent = workNotificationIntent(context, workerParameters, isAbortRequested = true),
		showGameIconInProgressNotification = true
	),
	WorkNotificationsParameters(successMessage, failureMessage)
)