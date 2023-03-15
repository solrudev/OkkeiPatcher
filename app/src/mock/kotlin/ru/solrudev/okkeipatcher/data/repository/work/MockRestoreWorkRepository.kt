package ru.solrudev.okkeipatcher.data.repository.work

import androidx.work.WorkManager
import ru.solrudev.okkeipatcher.R
import ru.solrudev.okkeipatcher.app.repository.work.RestoreWorkRepository
import ru.solrudev.okkeipatcher.app.repository.work.WorkRepository
import ru.solrudev.okkeipatcher.data.repository.app.work.UniqueWorkRepositoryImpl
import ru.solrudev.okkeipatcher.data.worker.MockWorker
import ru.solrudev.okkeipatcher.domain.core.LocalizedString
import javax.inject.Inject

private const val RESTORE_WORK_NAME = "RestoreWork"
private val workLabel = LocalizedString.resource(R.string.work_label_restoring)

class MockRestoreWorkRepository @Inject constructor(
	workRepository: WorkRepository,
	workManager: WorkManager
) : RestoreWorkRepository, UniqueWorkRepositoryImpl<MockWorker>(
	workName = RESTORE_WORK_NAME,
	workLabel = workLabel,
	workerClass = MockWorker::class.java,
	workRepository,
	workManager
)