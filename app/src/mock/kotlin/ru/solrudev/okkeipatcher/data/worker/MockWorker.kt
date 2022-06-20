package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import ru.solrudev.okkeipatcher.domain.service.operation.factory.MockOperationFactory

private val workLabel = LocalizedString.resource(R.string.notification_title_test)

@HiltWorker
class MockWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	preferencesRepository: PreferencesRepository
) : ForegroundWorker(
	context,
	workerParameters,
	MockOperationFactory(preferencesRepository.patchStatus, workerParameters.tags),
	workLabel
)