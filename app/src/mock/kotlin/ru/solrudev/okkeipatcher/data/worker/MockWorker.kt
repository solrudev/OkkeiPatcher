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

@HiltWorker
class MockWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	preferencesRepository: PreferencesRepository
) : ForegroundWorker(
	context,
	workerParameters,
	MockOperationFactory(preferencesRepository.isPatchedDao, workerParameters.tags)
) {

	override val workTitle = LocalizedString.resource(R.string.notification_title_test)

	override val destinationScreen: Int
		get() {
			if (tags.contains("PatchWork")) {
				return R.id.patch_fragment
			}
			if (tags.contains("RestoreWork")) {
				return R.id.restore_fragment
			}
			return R.id.home_fragment
		}
}