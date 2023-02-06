package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.PreferencesRepository
import ru.solrudev.okkeipatcher.data.service.factory.NotificationServiceFactory
import ru.solrudev.okkeipatcher.data.worker.model.WorkNotificationsParameters
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.core.Message
import ru.solrudev.okkeipatcher.domain.operation.factory.MockOperationFactory
import ru.solrudev.okkeipatcher.domain.repository.patch.factory.PatchRepositoryFactory

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
	patchRepositoryFactory: PatchRepositoryFactory
) : ForegroundOperationWorker(
	context, workerParameters, notificationServiceFactory, workManager,
	MockOperationFactory(
		patchRepositoryFactory,
		preferencesRepository.patchVersion,
		preferencesRepository.patchStatus,
		isPatchWork = "PatchWork" in workerParameters.tags
	),
	WorkNotificationsParameters(workLabel, successMessage, failureMessage)
)