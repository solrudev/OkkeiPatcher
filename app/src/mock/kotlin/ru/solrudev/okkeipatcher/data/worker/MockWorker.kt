package ru.solrudev.okkeipatcher.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.domain.model.LocalizedString
import ru.solrudev.okkeipatcher.domain.operation.AbstractOperation
import ru.solrudev.okkeipatcher.domain.repository.app.PreferencesRepository
import kotlin.time.Duration.Companion.seconds

@HiltWorker
class MockWorker @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted workerParameters: WorkerParameters,
	private val preferencesRepository: PreferencesRepository
) : ForegroundWorker(context, workerParameters) {

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

	override suspend fun getOperation() = object : AbstractOperation<Unit>() {

		private val stepsCount = 5
		override val progressMax = stepsCount * 100

		override suspend fun invoke() {
			repeat(stepsCount) { stepIndex ->
				val step = stepIndex + 1
				status(LocalizedString.raw(step.toString().repeat(10)))
				delay(1.seconds)
				progressDelta(100)
			}
			preferencesRepository.isPatchedDao.persist(tags.contains("PatchWork"))
		}
	}
}