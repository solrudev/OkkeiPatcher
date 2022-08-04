package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.data.worker.MockWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import ru.solrudev.okkeipatcher.domain.repository.work.PatchWorkRepository
import ru.solrudev.okkeipatcher.domain.repository.work.WorkRepository
import javax.inject.Inject

private const val PATCH_WORK_NAME = "PatchWork"
private val workLabel = LocalizedString.resource(R.string.work_label_patch)

class MockPatchWorkRepositoryImpl @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : PatchWorkRepository, ConcreteWorkRepositoryImpl<MockWorker>(
	workName = PATCH_WORK_NAME,
	workLabel = workLabel,
	workerClass = MockWorker::class.java,
	workRepository,
	workManager
)